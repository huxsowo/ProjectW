package ProjectW.Kits.KitTypes;

public enum KitType {
    UNDEFINED("UNDEFINED KIT TYPE"),
    BRUTE("Brute"),
    TANK("Tank"),
    CONTROL("Control"),
    SUPPORT("Support"),
    ASSASSIN("Assassin"),
    RANGER("Ranger");

    private String kitType;

    KitType(String kitType){this.kitType = kitType;}

    public String toString(){return kitType;}
}
