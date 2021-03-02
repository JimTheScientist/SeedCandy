package wearblackallday.gui.components.structurepanel;

import wearblackallday.gui.SeedCandy;
import wearblackallday.gui.components.TextBlock;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.seed.StructureSeed;
import wearblackallday.data.Strings;
import wearblackallday.swing.components.ButtonSet;
import wearblackallday.swing.components.GridPanel;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StructurePanel extends JPanel {

    public final TextBlock inputText;

    public StructurePanel() {
        this.inputText = new TextBlock(true);
        TextBlock outputText = new TextBlock(false);
        JPanel inputPanel = new JPanel(new BorderLayout());
        GridPanel<BiomeUnit> biomePanel = new GridPanel<>(16, 1, BiomeUnit::new);
        JPanel selectionPanel = new JPanel(new GridLayout(2, 2));
        ButtonSet<JButton> buttonSet = new ButtonSet<>(JButton::new,
                "reverse to nextLong()", "crack with Biomes", "verify WorldSeeds");
        JProgressBar progressBar = new JProgressBar(0,1);

        this.setLayout(new BorderLayout());
        this.setName("StructureSeed");

        buttonSet.addListeners(
                longButton -> {
                    outputText.setText("");
                    for (long seed : Strings.splitToLongs(this.inputText.getText())) {
                        StructureSeed.toRandomWorldSeeds(seed).forEach(worldseed -> outputText.addEntry(String.valueOf(worldseed)));
                    }
                },
                biomeButton -> {
                    outputText.setText("");
                    progressBar.setMaximum(Strings.countLines(this.inputText.getText()) * 65536);
                    AtomicInteger progress = new AtomicInteger(0);
                    SeedCandy.POOL.execute(Strings.splitToLongs(this.inputText.getText()), seed -> StructureSeed.getWorldSeeds(seed).forEachRemaining(candidate -> {
                        OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_2, candidate);
                        progressBar.setValue(progress.incrementAndGet());
                        boolean match = true;
                        for (int i = 0; i < 16; i++) {
                            if (!biomePanel.getComponent(i, 0).matches(biomeSource)) {
                                match = false;
                                break;
                            }
                        }
                        if(match) SwingUtilities.invokeLater(() -> outputText.addEntry(String.valueOf(candidate)));
                    }));
                },
                verifyButton -> {
                    outputText.setText("");
                    for (long seed : Strings.splitToLongs(this.inputText.getText())) {
                        OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_2, seed);
                        boolean match = true;
                        for (int i = 0; i < 16; i++) {
                            if (!biomePanel.getComponent(i, 0).matches(biomeSource)) {
                                match = false;
                                break;
                            }
                        }
                        if(match) outputText.addEntry(String.valueOf(seed));
                    }
                }
        );

        buttonSet.addAll(selectionPanel);
        selectionPanel.add(progressBar);
        inputPanel.add(biomePanel, BorderLayout.CENTER);
        inputPanel.add(selectionPanel, BorderLayout.SOUTH);
        this.add(this.inputText, BorderLayout.WEST);
        this.add(outputText, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.EAST);
    }
}
