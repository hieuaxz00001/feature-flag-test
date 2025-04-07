package com.example.demo2.utils;

import java.lang.module.ModuleDescriptor;
import java.math.BigInteger;

public class Utils {
    public static boolean compareVersion(String expectedVersion , String actualVersion){
        var version1 = ModuleDescriptor.Version.parse(expectedVersion);
        var version2 = ModuleDescriptor.Version.parse(actualVersion);
        return version1.compareTo(version2) > 0;
    }
}
