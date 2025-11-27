package de.jerome.whatsthesongsname.spigot.object;

public enum Messages {

    SYNTAX_INFO("syntax.info"),
    SYNTAX_JOIN("syntax.join"),
    SYNTAX_LEAVE("syntax.leave"),
    SYNTAX_RELOAD("syntax.reload"),
    SYNTAX_STATS_OWN("syntax.stats.own"),
    SYNTAX_STATS_OTHER("syntax.stats.other"),
    SYNTAX_NO_PERMISSION("syntax.noPermission"),

    INFO("info"),

    JOIN_ALREADY_IN_GAME("join.alreadyInGame"),
    JOIN_JOINED("join.joined"),
    JOIN_WAITING("join.waiting"),

    LEAVE_NOT_IN_GAME("leave.notInGame"),
    LEAVE_LEFT("leave.left"),
    LEAVE_PLAYS_EXCEEDED("leave.playsExceeded"),

    ACTIONBAR_PLAYING("actionbar.playing"),
    ACTIONBAR_CHOOSING("actionbar.choosing"),
    ACTIONBAR_WAITING("actionbar.waiting"),

    RELOAD_SUCCESS("reload.success"),
    RELOAD_FAILED("reload.failed"),

    STATS_OWN("stats.own"),
    STATS_OTHER_SUCCESS("stats.other.success"),
    STATS_OTHER_PLAYER_NOT_FOUND("stats.other.playerNotFound"),
    STATS_PLAYS_RESET("stats.plays.reset"),

    CHOSE_EVALUATION_SONG_NAME("choseEvaluation.songName"),
    CHOSE_EVALUATION_CORRECT_ANSWER("choseEvaluation.correctAnswer"),
    CHOSE_EVALUATION_WRONG_ANSWER("choseEvaluation.wrongAnswer"),
    CHOSE_EVALUATION_NO_ANSWER("choseEvaluation.noAnswer"),

    SUBMIT_ANSWER("submitAnswer"),

    INVENTORY_TITLE("inventory.title"),
    INVENTORY_SONG_ITEMS_DISPLAYNAME_COLOR("inventory.songItems.displaynameColor"),
    INVENTORY_SONG_ITEMS_LORE("inventory.songItems.lore");

    private final String path;

    Messages(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
