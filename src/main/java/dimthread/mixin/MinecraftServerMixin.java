package dimthread.mixin;

import dimthread.DimThread;
import dimthread.api.ThreadPool;
import dimthread.util.CrashInfo;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Shadow private int ticks;
	@Shadow private PlayerManager playerManager;
	@Shadow public abstract Iterable<ServerWorld> getWorlds();

	@Shadow protected abstract ServerWorld[] getWorldArray();

	/**
	 * Returns an empty iterator to stop {@code MinecraftServer#tickWorlds} from ticking
	 * dimensions. This behaviour is overwritten below.
	 *
	 * @see MinecraftServerMixin#tickWorlds(BooleanSupplier, CallbackInfo)
	 * */
	@Redirect(method = "tickWorlds", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/server/MinecraftServer;getWorldArray()[Lnet/minecraft/server/world/ServerWorld;"))
	public ServerWorld[] tickWorlds(MinecraftServer instance) {
		return DimThread.MANAGER.isActive((MinecraftServer)(Object)this) ?  new ServerWorld[]{} : getWorldArray();
	}

	/**
	 * Distributes world ticking over 3 worker threads (one for each dimension) and waits until
	 * they are all complete.
	 * */
	@Inject(method = "tickWorlds", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/server/MinecraftServer;getWorldArray()[Lnet/minecraft/server/world/ServerWorld;"))
	public void tickWorlds(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		if(!DimThread.MANAGER.isActive((MinecraftServer)(Object)this))return;

		AtomicReference<CrashInfo> crash = new AtomicReference<>();
		ThreadPool pool = DimThread.getThreadPool((MinecraftServer)(Object)this);

		pool.execute(this.getWorlds().iterator(), serverWorld -> {
			DimThread.attach(Thread.currentThread(), serverWorld);

			if(this.ticks % 20 == 0) {
				WorldTimeUpdateS2CPacket timeUpdatePacket = new WorldTimeUpdateS2CPacket(
						serverWorld.getTime(), serverWorld.getTimeOfDay(),
						serverWorld.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE));

				this.playerManager.sendToDimension(timeUpdatePacket, serverWorld.getRegistryKey());
			}

			DimThread.swapThreadsAndRun(() -> {
				BasicEventHooks.onPreWorldTick(serverWorld);
				try {
					serverWorld.tick(shouldKeepTicking);
				} catch(Throwable throwable) {
					crash.set(new CrashInfo(serverWorld, throwable));
				}
				BasicEventHooks.onPostWorldTick(serverWorld);
			}, serverWorld, serverWorld.getChunkManager());
		});

		pool.awaitCompletion();

		if(crash.get() != null) {
			crash.get().crash("Exception ticking world");
		}
	}

	/**
	 * Shutdown all threadpools when the server stop.
	 * Prevent server hang when stopping the server.
	 * */
	@Inject(method = "shutdown", at = @At("HEAD"))
	public void shutdownThreadpool(CallbackInfo ci) {
		DimThread.MANAGER.threadPools.forEach((server, pool) -> pool.shutdown());
	}
}
