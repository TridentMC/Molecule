package com.tridevmc.molecule.network;

import com.tridevmc.compound.network.message.Message;
import com.tridevmc.molecule.Molecule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Random;

public class TestMessage extends Message {

    public float myFloat;
    public double myDouble;
    public byte myByte;
    public short myShort;
    public int myInt;
    public long myLong;

    public boolean myBoolean;
    public boolean myOtherBoolean;
    public char myChar;
    public String myString;

    public ItemStack myItemStack;
    public CompoundNBT myTag;
    public BlockPos myPos;

    public Direction myFacing;

    public TestMessage() {
        this(false);
    }

    public TestMessage(boolean genValues) {
        if (genValues) {
            this.myFloat = Float.MAX_VALUE;
            this.myDouble = Double.MAX_VALUE;
            this.myByte = Byte.MAX_VALUE;
            this.myShort = Short.MAX_VALUE;
            this.myInt = Integer.MAX_VALUE;
            this.myLong = Long.MAX_VALUE;

            this.myBoolean = true;
            this.myOtherBoolean = false;
            this.myChar = 'a';
            this.myString = "haha YES";

            this.myItemStack = Items.TRIDENT.getDefaultInstance();
            this.myTag = new CompoundNBT();
            this.myTag.putFloat("Float", 4F);
            Random rand = new Random();
            this.myPos = new BlockPos(rand.nextInt(511), rand.nextInt(255), rand.nextInt(511));
            this.myFacing = Direction.random(rand);

            Molecule.LOG.info("Sending {}, {}", this.getClass().getName(), this.toString());
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, new MultilineRecursiveToStringStyle())
                .append("myFloat", this.myFloat)
                .append("myDouble", this.myDouble)
                .append("myByte", this.myByte)
                .append("myShort", this.myShort)
                .append("myInt", this.myInt)
                .append("myLong", this.myLong)
                .append("myBoolean", this.myBoolean)
                .append("myOtherBoolean", this.myOtherBoolean)
                .append("myChar", this.myChar)
                .append("myString", this.myString)
                .append("myItemStack", this.myItemStack.getItem() + ":" + this.myItemStack.getTag())
                .append("myTag", this.myTag)
                .append("myPos", this.myPos)
                .append("myFacing", this.myFacing.getName())
                .toString();
    }

    @Override
    public void handle(PlayerEntity player) {
        Molecule.LOG.info("Received {}, {}", this.getClass().getName(), this.toString());
    }
}
