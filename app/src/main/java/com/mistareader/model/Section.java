package com.mistareader.model;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.annotation.JsonObject.FieldDetectionPolicy;
import com.mistareader.util.Empty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@JsonObject(fieldDetectionPolicy = FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class Section {
    @JsonField(name = "shortn")
    public String sectionShortName;
    @JsonField(name = "fulln")
    public String sectionFullName;
    @JsonField(name = "forum")
    public String forumName;
    @JsonField(name = "id")
    public String sectionId;

    public static String getSectionID(String strValue) {
        return "15";//dummy until server side will return proper ID's
    }

    public static ArrayList<String> getUniqueForums(List<Section> sections) {
        if (Empty.is(sections)) return null;

        HashSet<String> hs = new HashSet<>();
        for (Section section : sections) {
            hs.add(section.forumName);
        }

        ArrayList<String> forumsUnique = new ArrayList<>(hs);
        Collections.sort(forumsUnique, String::compareTo);

        return forumsUnique;
    }

    public static ArrayList<String> getSectionsListForForum(ArrayList<Section> sections, ArrayList<String> forums, int forumIndex) {

        String selectedForumName = forums.get(forumIndex);
        ArrayList<String> resList = new ArrayList<String>();

        for (int i = 0; i < sections.size(); i++) {
            Section sec = sections.get(i);
            if (sec.forumName.equals(selectedForumName)) {
                resList.add(sec.sectionFullName);
            }

        }

        return resList;
    }

    public static String getSectionsAsString(ArrayList<Section> sections) {
        try {
            return LoganSquare.serialize(sections, Section.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Section> getSectionsFromString(String sSections) {
        if (Empty.is(sSections)) {
            return null;
        }
        try {
            return (ArrayList<Section>) LoganSquare.parseList(sSections, Section.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Section> fillDefauiltSectionsList() {
        ArrayList sections = new ArrayList(44);

        Section section = new Section();
        section.sectionId = "3";
        section.sectionFullName = "1С 7.7 и ранее";
        section.sectionShortName = "v7";
        section.forumName = "1C";
        sections.add(section);

        section = new Section();
        section.sectionId = "8";
        section.sectionFullName = "1С 8";
        section.sectionShortName = "v8";
        section.forumName = "1C";
        sections.add(section);

        section = new Section();
        section.sectionId = "15";
        section.sectionFullName = "Админ";
        section.sectionShortName = "admin";
        section.forumName = "IT";
        sections.add(section);

        section = new Section();
        section.sectionId = "24";
        section.sectionFullName = "Мобильный мир";
        section.sectionShortName = "mobile";
        section.forumName = "IT";
        sections.add(section);

        section = new Section();
        section.sectionId = "1";
        section.sectionFullName = "IT-новости";
        section.sectionShortName = "it-news";
        section.forumName = "IT";
        sections.add(section);

        section = new Section();
        section.sectionId = "10";
        section.sectionFullName = "Математика и алгоритмы";
        section.sectionShortName = "math";
        section.forumName = "IT";
        sections.add(section);

        section = new Section();
        section.sectionId = "19";
        section.sectionFullName = "Unix / Linux";
        section.sectionShortName = "nix";
        section.forumName = "IT";
        sections.add(section);

        section = new Section();
        section.sectionId = "4";
        section.sectionFullName = "Веб-мастеринг";
        section.sectionShortName = "web";
        section.forumName = "IT";
        sections.add(section);

        section = new Section();
        section.sectionId = "13";
        section.sectionFullName = "Политика";
        section.sectionShortName = "politic";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "23";
        section.sectionFullName = "Как страшно жить";
        section.sectionShortName = "fear";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "5";
        section.sectionFullName = "Работа";
        section.sectionShortName = "job";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "6";
        section.sectionFullName = "Жизнь форума";
        section.sectionShortName = "forum";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "31";
        section.sectionFullName = "Жизнь прекрасна";
        section.sectionShortName = "good";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "25";
        section.sectionFullName = "За рулём";
        section.sectionShortName = "car";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "7";
        section.sectionFullName = "Юмор";
        section.sectionShortName = "lol";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "32";
        section.sectionFullName = "Игры";
        section.sectionShortName = "games";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "26";
        section.sectionFullName = "Психология";
        section.sectionShortName = "love";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "2";
        section.sectionFullName = "Философия";
        section.sectionShortName = "philosophy";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "28";
        section.sectionFullName = "Культура";
        section.sectionShortName = "culture";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "29";
        section.sectionFullName = "Наука";
        section.sectionShortName = "science";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "20";
        section.sectionFullName = "Спорт";
        section.sectionShortName = "sport";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "27";
        section.sectionFullName = "Поесть и выпить";
        section.sectionShortName = "food";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "36";
        section.sectionFullName = "Примечательные события";
        section.sectionShortName = "events";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "33";
        section.sectionFullName = "Спам";
        section.sectionShortName = "spam";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "18";
        section.sectionFullName = "Цифровое фото";
        section.sectionShortName = "digit-photo";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "38";
        section.sectionFullName = "Недвижимость";
        section.sectionShortName = "realty";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "39";
        section.sectionFullName = "Забегаловка";
        section.sectionShortName = "chat";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "40";
        section.sectionFullName = "История";
        section.sectionShortName = "history";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "41";
        section.sectionFullName = "Путешествия";
        section.sectionShortName = "travel";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "42";
        section.sectionFullName = "Английский";
        section.sectionShortName = "english";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "43";
        section.sectionFullName = "Доминикана";
        section.sectionShortName = "dominikana";
        section.forumName = "LIFE";
        sections.add(section);

        section = new Section();
        section.sectionId = "44";
        section.sectionFullName = "Отдам даром";
        section.sectionShortName = "darom";
        section.forumName = "LIFE";
        sections.add(section);

        return sections;
    }
}
