package codes.biscuit.skyblockaddons.asm;

import codes.biscuit.skyblockaddons.asm.utils.TransformerClass;
import codes.biscuit.skyblockaddons.asm.utils.TransformerMethod;
import codes.biscuit.skyblockaddons.tweaker.transformer.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class WorldClientTransformer implements ITransformer {

    /**
     * {@link net.minecraft.client.multiplayer.WorldClient}
     */
    @Override
    public String[] getClassName() {
        return new String[]{TransformerClass.WorldClient.getTransformerName()};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            if (TransformerMethod.onEntityRemoved.matches(methodNode)) {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), onEntityRemoved());
            }
            else if (TransformerMethod.invalidateRegionAndSetBlock.matches(methodNode)) {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), insertBlockUpdated());
            }
        }
    }

    private InsnList onEntityRemoved() {
        InsnList list = new InsnList();

        list.add(new VarInsnNode(Opcodes.ALOAD, 1)); // entityIn
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "codes/biscuit/skyblockaddons/asm/hooks/WorldClientHook", "onEntityRemoved",
                "("+TransformerClass.Entity.getName()+")V", false)); // WorldClientHook.onEntityRemoved(entityIn);

        return list;
    }

    private InsnList insertBlockUpdated() {
        InsnList list = new InsnList();

        list.add(new VarInsnNode(Opcodes.ALOAD, 1)); // pos
        list.add(new VarInsnNode(Opcodes.ALOAD, 2)); // state
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "codes/biscuit/skyblockaddons/asm/hooks/WorldClientHook", "blockUpdated",
                "("+TransformerClass.BlockPos.getName()+TransformerClass.IBlockState.getName()+")V", false)); // WorldClientHook.blockUpdated(pos, state);

        return list;
    }
}