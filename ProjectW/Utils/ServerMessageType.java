package ProjectW.Utils;

public enum ServerMessageType {
    GAME("§4Game§8 »§f"),
    RECHARGE("§4Recharge§8 »§f"),
    DEATH("§4Death§8 »§f"),
    ABILITY("§4Ability§8 »§f"),
    POWERUP("§4Powerup§8 »§f"),
    SERVER("§4Server§8 »§f"),
    DEBUG("§4DEBUG§8 »§c");

    private String message;

    ServerMessageType(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }

}
