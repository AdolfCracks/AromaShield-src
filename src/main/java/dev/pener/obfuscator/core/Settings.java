package dev.pener.obfuscator.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Settings {

    public List<String> packagesToRename = new ArrayList<>();
    public List<String> excludeFromRenaming = new ArrayList<>();

    public List<String> fullExclude = new ArrayList<>();
    public String renameTo = "short";


    public Settings addPackagesToRename(String... packageNames){
        packagesToRename.addAll(Arrays.stream(packageNames).collect(Collectors.toList()));
        return this;
    }

    public boolean shouldRename(String className){
        if(excludeFromRenaming.stream().anyMatch(className::startsWith)) return false;
        return packagesToRename.stream().anyMatch(className::startsWith);
    }
    public Settings renameTo(String renameTo){
        this.renameTo = renameTo;
        return this;
    }

    public boolean shouldTransform(String className) {
        if(fullExclude.stream().anyMatch(className::startsWith)) return false;
        return true;
    }

    public Settings fullExclude(String s) {
        fullExclude.add(s);
        excludeFromRenaming.add(s);
        return this;
    }
    public Settings excludeFromRenaming(String s) {
        excludeFromRenaming.add(s);
        return this;
    }
}