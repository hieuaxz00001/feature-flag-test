package com.example.demo2;

import java.lang.module.ModuleDescriptor;

public class Utils {
    public static boolean compareVersion(String expectedVersion , String actualVersion){
        var version1 = ModuleDescriptor.Version.parse(expectedVersion);
        var version2 = ModuleDescriptor.Version.parse(actualVersion);
        return version1.compareTo(version2) > 0;
    }
}
