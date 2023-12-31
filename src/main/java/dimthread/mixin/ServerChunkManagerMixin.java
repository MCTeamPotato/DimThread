package dimthread.mixin;

import dimthread.DimThread;
import dimthread.thread.IMutableMainThread;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ServerChunkManager.class, priority = 1001)
public abstract class ServerChunkManagerMixin extends ChunkManager implements IMutableMainThread {

	@Shadow @Final @Mutable private Thread serverThread;
	@Shadow @Final
	public ServerWorld world;

	@Override
	public Thread getMainThread() {
		return this.serverThread;
	}

	@Override
	public void setMainThread(Thread thread) {
		this.serverThread = thread;
	}

	@Redirect(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
	public Thread currentThread(int x, int z, ChunkStatus leastStatus, boolean create) {
		Thread thread = Thread.currentThread();

		if(DimThread.MANAGER.isActive(this.world.getServer()) && DimThread.owns(thread)) {
			return this.serverThread;
		}

		return thread;
	}
}
