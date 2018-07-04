package io.github.phantamanta44.libnine.component;

import io.github.phantamanta44.libnine.util.data.ByteUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.BiConsumer;

public class FluidReservoir extends IntReservoir implements IFluidTankProperties {

    private final boolean locked;
    private final Collection<BiConsumer<Fluid, Fluid>> callbacks;

    private Fluid fluid;

    public FluidReservoir(Fluid fluid, int qty, int capacity, boolean locked) {
        super(qty, capacity);
        this.fluid = fluid;
        this.locked = locked;
        this.callbacks = new LinkedList<>();
    }

    public FluidReservoir(Fluid fluid, int capacity) {
        super(0, capacity);
        this.fluid = fluid;
        this.locked = true;
        this.callbacks = new LinkedList<>();
    }

    public FluidReservoir(int capacity) {
        super(0, capacity);
        this.fluid = null;
        this.locked = false;
        this.callbacks = new LinkedList<>();
    }

    public Fluid getFluid() {
        return fluid;
    }

    public void setFluid(Fluid fluid) {
        if (locked) throw new UnsupportedOperationException("Fluid type is locked!");
        Fluid oldFluid = this.fluid;
        this.fluid = fluid;
        callbacks.forEach(c -> c.accept(oldFluid, this.fluid));
    }

    @Override
    public void setQuantity(int qty) {
        super.setQuantity(qty);
        if (qty == 0 && !locked) fluid = null;
    }

    @Nullable
    @Override
    public FluidStack getContents() {
        return fluid != null ? new FluidStack(getFluid(), getQuantity()) : null;
    }

    @Override
    public boolean canFill() {
        return true;
    }

    @Override
    public boolean canDrain() {
        return true;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluidStack) {
        return canFill() && (fluid == null || fluid == fluidStack.getFluid());
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluidStack) {
        return canDrain() && fluid != null && fluid == fluidStack.getFluid();
    }

    public void onFluidChange(BiConsumer<Fluid, Fluid> callback) {
        callbacks.add(callback);
    }

    @Override
    public void serializeNBT(NBTTagCompound tag) {
        super.serializeNBT(tag);
        if (!locked) tag.setString("Fluid", fluid.getName());
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        super.deserializeNBT(tag);
        if (!locked) fluid = FluidRegistry.getFluid(tag.getString("Fluid"));
    }

    @Override
    public void serializeBytes(ByteUtils.Writer data) {
        super.serializeBytes(data);
        if (!locked) data.writeFluid(getFluid());
    }

    @Override
    public void deserializeBytes(ByteUtils.Reader data) {
        super.deserializeBytes(data);
        if (!locked) setFluid(data.readFluid());
    }

}
