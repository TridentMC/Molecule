package com.tridevmc.molecule;

import com.tridevmc.compound.config.ConfigType;
import com.tridevmc.compound.config.ConfigValue;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigType(ModConfig.Type.COMMON)
public class MoleculeConfig {

    @ConfigValue(comment = "Test Integer")
    public int testInt = 32;

    @ConfigValue(comment = "Test Double")
    public double testDouble = 32.32D;

    @ConfigValue(comment = "Test Boolean")
    public boolean testBoolean = false;

    @ConfigValue(comment = "Test String")
    public String testString = "Thirty-Two";

    @ConfigValue(comment = "Test Integer Array")
    public int[] testIntArray = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    @ConfigValue(comment = "Test Double Array")
    public double[] testDoubleArray = new double[]{0.01D, 0.02D, 0.03D, 0.04D, 0.05D, 0.06D, 0.07D, 0.08D, 0.09D, 0.1D};

    @ConfigValue(comment = "Test Boolean Array")
    public boolean[] testBooleanArray = new boolean[]{true, false, true, false};

    @ConfigValue(comment = "Test String Array")
    public String[] testStringArray = new String[]{"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"};

    @ConfigValue(comment = "Test Integer List")
    public List<Integer> testIntList = new ArrayList<>(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));

    @ConfigValue(comment = "Test Double List")
    public List<Double> testDoubleList = new ArrayList<>(Arrays.asList(new Double[]{0.01D, 0.02D, 0.03D, 0.04D, 0.05D, 0.06D, 0.07D, 0.08D, 0.09D, 0.1D}));

    @ConfigValue(comment = "Test Boolean List")
    public List<Boolean> testBooleanList = new ArrayList<>(Arrays.asList(new Boolean[]{true, false, true, false}));

    @ConfigValue(comment = "Test String List")
    public List<String> testStringList = new ArrayList<>(Arrays.asList(new String[]{"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"}));


}
