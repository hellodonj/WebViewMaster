package com.ijkplayer.utils;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkPlayerUtils {

    public static IjkMediaPlayer newPlayer() {
        IjkMediaPlayer player = new IjkMediaPlayer();
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);

        return player;
    }

}
