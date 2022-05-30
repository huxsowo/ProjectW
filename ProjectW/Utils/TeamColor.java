package ProjectW.Utils;

import org.bukkit.Material;

public enum TeamColor {
    RED("§l§cRed", Material.RED_WOOL),
    BLUE("§l§9Blue", Material.BLUE_WOOL);

    String name;
    Material blockMaterial;

    TeamColor(String name, Material blockMaterial){
        this.name = name;
        this.blockMaterial = blockMaterial;
    }

    public String getName(){return name;}

    public Material getBlockMaterial(){return blockMaterial;}
}
