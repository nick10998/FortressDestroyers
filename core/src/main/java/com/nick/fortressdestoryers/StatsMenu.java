package com.nick.fortressdestoryers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.List;

public class StatsMenu extends Table {
    private final ScrollPane dataScroller;

    public StatsMenu(Texture whiteTexture, BitmapFont font, List<String[]> getData, Table center) {
        this.setBackground(new TextureRegionDrawable(new TextureRegion(whiteTexture)).tint(new Color(0.1f, 0.1f, 0.15f, 0.85f)));
        this.center();
        this.setSize(1200f, 550f);
        BitmapFont statsFont = new BitmapFont();
        statsFont.getData().setScale(3f);
        Label.LabelStyle dataRow = new Label.LabelStyle(statsFont, Color.WHITE);
        Label.LabelStyle topRow = new Label.LabelStyle(statsFont, Color.YELLOW);

        //labels for top row
        Table header = new Table();
        Label levelNumberHead = new Label("Level Number", topRow);
        Label highscoreHead = new Label("High-Score", topRow);
        Label statusHead = new Label("Completion Status", topRow);

        header.add(levelNumberHead).width(300f).align(Align.left).pad(10f);
        header.add(highscoreHead).width(300f).align(Align.center).pad(10f);
        header.add(statusHead).width(300f).align(Align.right).pad(10f);

        Table data = new Table();
        if (getData == null || getData.isEmpty()) {
            data.add(new Label("No data available", dataRow)).colspan(3).center().pad(20f);
            data.row();
        } else {
            for (String[] row : getData) {
                String levelText = row[0];
                String scoreText = row[1];
                String statusText = row[2];

                //make completion either green or gray
                Label.LabelStyle statusStyle = new Label.LabelStyle(statsFont, statusText.equals("Completed") ? Color.GREEN : Color.GRAY);

                data.add(new Label(levelText, dataRow)).pad(10f).align(Align.left);
                data.add(new Label(scoreText, dataRow)).pad(10f).align(Align.center);
                data.add(new Label(statusText, statusStyle)).pad(10f).align(Align.right);
                data.row();
            }
        }
        dataScroller = new ScrollPane(data);
        dataScroller.setFadeScrollBars(false);
        dataScroller.setScrollingDisabled(true, false);

        //button to close the stats menu
        TextButton.TextButtonStyle closeStyle = new TextButton.TextButtonStyle();
        closeStyle.font = font;
        closeStyle.fontColor = Color.BLACK;
        closeStyle.up = new TextureRegionDrawable(new TextureRegion(whiteTexture))
            .tint(new Color(0.8f, 0.2f, 0.2f, 0.9f));

        TextButton closeButton = new TextButton("Exit", closeStyle);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
                //this allows main menu buttons to be clicked again
                if (center != null) {
                    center.setTouchable(Touchable.enabled);
                }
                //this stops the scroller from being clickable
                if (getStage() != null) {
                    getStage().setScrollFocus(null);
                }
            }
        });

        //puts header table plus scrollable data table and exit button
        this.add(header).padTop(20f).row();
        this.add(dataScroller).width(950f).height(400f).pad(10f).row();
        this.add(closeButton).width(200f).height(60f).padBottom(10f);

        this.setVisible(false);
    }

    public ScrollPane getDataScroller () {
        return dataScroller;
    }
}

