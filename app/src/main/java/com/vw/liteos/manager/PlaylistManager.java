package com.vw.liteos.manager;

import com.vw.liteos.model.IPTVChannel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaylistManager {
    private static final Pattern TVG_ID_PATTERN = Pattern.compile("tvg-id=\"([^\"]*)\"");
    private static final Pattern TVG_NAME_PATTERN = Pattern.compile("tvg-name=\"([^\"]*)\"");
    private static final Pattern TVG_LOGO_PATTERN = Pattern.compile("tvg-logo=\"([^\"]*)\"");
    private static final Pattern GROUP_TITLE_PATTERN = Pattern.compile("group-title=\"([^\"]*)\"");

    public static List<IPTVChannel> parseM3U(InputStream inputStream) {
        List<IPTVChannel> channels = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return parseFromReader(reader);
        } catch (Exception ignored) {
            return channels;
        }
    }

    public static List<IPTVChannel> parseM3U(String content) {
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            return parseFromReader(reader);
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }

    private static List<IPTVChannel> parseFromReader(BufferedReader reader) throws Exception {
        List<IPTVChannel> channels = new ArrayList<>();
        String line;
        String curId = "";
        String curName = "";
        String curLogo = "";
        String curGroup = "General";

        int idCounter = 1;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("#EXTINF:")) {
                curId = extractPattern(TVG_ID_PATTERN, line);
                curName = extractPattern(TVG_NAME_PATTERN, line);
                curLogo = extractPattern(TVG_LOGO_PATTERN, line);
                curGroup = extractPattern(GROUP_TITLE_PATTERN, line);

                if (curGroup.isEmpty()) curGroup = "General";

                // Channel name is after the last comma
                int commaIndex = line.lastIndexOf(',');
                if (commaIndex != -1 && commaIndex < line.length() - 1) {
                    String title = line.substring(commaIndex + 1).trim();
                    if (!title.isEmpty()) {
                        curName = title;
                    }
                }
                if (curId.isEmpty()) {
                    curId = "ch_" + (idCounter++);
                }
            } else if (!line.startsWith("#")) {
                // Stream URL
                if (!line.isEmpty()) {
                    if (curName.isEmpty()) {
                        curName = "Channel " + (channels.size() + 1);
                    }
                    IPTVChannel ch = new IPTVChannel(curId, curName, curLogo, line, curGroup, "IN", curGroup);
                    channels.add(ch);

                    curId = "";
                    curName = "";
                    curLogo = "";
                    curGroup = "General";
                }
            }
        }
        return channels;
    }

    private static String extractPattern(Pattern pattern, String text) {
        Matcher m = pattern.matcher(text);
        if (m.find()) {
            return m.group(1).trim();
        }
        return "";
    }
}
