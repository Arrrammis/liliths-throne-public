package com.lilithsthrone.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.lilithsthrone.controller.MainController;
import com.lilithsthrone.controller.TooltipUpdateThread;
import com.lilithsthrone.game.Game;
import com.lilithsthrone.game.Properties;
import com.lilithsthrone.game.PropertyValue;
import com.lilithsthrone.game.character.CharacterImportSetting;
import com.lilithsthrone.game.character.CharacterUtils;
import com.lilithsthrone.game.character.PlayerCharacter;
import com.lilithsthrone.game.character.body.valueEnums.Femininity;
import com.lilithsthrone.game.character.gender.Gender;
import com.lilithsthrone.game.character.persona.NameTriplet;
import com.lilithsthrone.game.character.quests.QuestLine;
import com.lilithsthrone.game.character.race.RaceStage;
import com.lilithsthrone.game.character.race.Subspecies;
import com.lilithsthrone.game.dialogue.DialogueNodeOld;
import com.lilithsthrone.game.dialogue.DialogueNodeType;
import com.lilithsthrone.game.dialogue.responses.Response;
import com.lilithsthrone.game.dialogue.story.CharacterCreation;
import com.lilithsthrone.game.dialogue.utils.OptionsDialogue;
import com.lilithsthrone.game.inventory.enchanting.TFEssence;
import com.lilithsthrone.game.sex.Sex;
import com.lilithsthrone.utils.Colour;
import com.lilithsthrone.utils.CreditsSlot;
import com.lilithsthrone.world.Generation;
import com.lilithsthrone.world.WorldType;
import com.lilithsthrone.world.places.PlaceType;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * @since 0.1.0
 * @version 0.2.11
 * @author Innoxia
 */
public class Main extends Application {

	public static Game game;
	public static Sex sexEngine;

	public static MainController mainController;

	public static Scene mainScene;

	public static Stage primaryStage;
	public static String author = "Innoxia";

	public static final String VERSION_NUMBER = "0.2.11.5";
	public static final String VERSION_DESCRIPTION = "Alpha";
	
	/**
	 * To turn it on, just add -Ddebug=true to java's VM options. (You should be able to do this in Eclipse through Run::Run Configurations...::Arguments tab::VM Arguments).
	 * Help page: https://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.pde.doc.user%2Fguide%2Ftools%2Flaunchers%2Farguments.htm
	 *  Or, from the command line java -Ddebug=true -jar LilithsThrone.jar
	 */
	public final static boolean DEBUG = Boolean.valueOf(System.getProperty("debug", "false"));

	public static final Image WINDOW_IMAGE = new Image("/com/lilithsthrone/res/images/windowIcon32.png");
	
	private static Properties properties;
	
	public static String patchNotes =
		
		"<p>"
			+ "Hello again!"
		+ "</p>"
			
		+ "<p>"
			+ "I've got the majority of the fortress framework done, but the imp citadel (the one in the very middle) is going to play a role in the main story a little larger than I originally planned."
			+ " For that reason, it's still under construction, but will be finished for 0.3, along with all of the fortress dialogue and Lyssieth's palace."
		+ "</p>"
			
		+ "<p>"
			+ "I expect the work on 0.3 to take at least a week, so the full version will be out some time between 9th-13th November."
		+ "</p>"
			
		+ "<p>"
			+ "I've completely run out of time, and so haven't had the chance to thoroughly test this version (outside of the normal testing that I do alongside adding new stuff),"
				+ " so if there are any major bugs/issues, I'll try to get them fixed for a hotfix by weekend's end."
		+ "</p>"
			
		+ "<p>"
			+ "Thank you all for playing Lilith's Throne! And a very big thank you to all the people supporting me on Patreon!"
			+ " If you wanted to ask me any specific questions about the game, you can either find me on my blog, or on the Lilith's Throne Discord. You can find a link to the discord on my blog. ^^"
		+ "</p>"
			
		+ "<br/>"

		+ "<list>"
			+ "<h6>v0.2.11.5</h6>"
			+"<li>Gameplay:</li>"
			+"<ul>Added framework for imp fortresses, with (very rough) placeholder dialogue. (Central imp citadel is still closed - it will be ready for the next update!)</ul>"
			+"<ul>Added next stage of main quest to the guards at Lyssieth's palace gate.</ul>"

			+"<li>Items:</li>"
			+"<ul>Added 'gothic dress', 'gothic sleeveless dress', and 'gothic heels' into the base game. (The items that were released as a mod on Halloween.)</ul>"
			+"<ul>Added several new weapons and items of clothing that I had planned for the imp fortress bosses. These are as follows:</ul>"
			+"<ul>Added weapon 'knuckle dusters'. (Sold by Vicky.)</ul>"
			+"<ul>Added weapon 'katana'. (Sold by Vicky. Daisho set.)</ul>"
			+"<ul>Added weapon 'wakizashi'. (Sold by Vicky. Daisho set.)</ul>"
			+"<ul>Added clothing 'striped gloves'. (Hand slot. Sold by Nyan.)</ul>"
			+"<ul>Added clothing 'men's hakama'. (Leg slot. Ronin set. Reindeer gift item, and worn by one of the imp fortress bosses.)</ul>"
			+"<ul>Added three new clothing items (for eyes, over-torso, and neck), and a new melee weapon, all of which are only found being worn/equipped by the central imp citadel's boss. (Not specified to avoid spoilers.)</ul>"

			+"<li>Other:</li>"
			+"<ul>Added new 'quest' rarity, and a new tab in your inventory for quest-related items. (Only Arthur's package and the new imp keys use this at the moment.)</ul>"
			+"<ul>You can no longer sell or drop quest items.</ul>"
			+"<ul>Rarity is now displayed in item/clothing/weapon tooltips.</ul>"
			+"<ul>Improved 'Potion effects' status effect icon.</ul>"
			+"<ul>Item tooltips now have the same format as weapons and clothing.</ul>"
			+"<ul>Added options to change number of arm pairs, eye pairs, horn rows, and tails in the slime, demon, and debug transform menus.</ul>"
			+"<ul>Arcane storm vulnerability is no longer bound to a character's race, but is instead applied when their 'arcane' attribute is less than 20. (The player is always immune.)</ul>"
			+"<ul>Added colouring options for all body part coverings in the slime/demon self-transform menu, and included ass, underarm, pubic, and facial hair colourings and lengths (if the relevant content option is turned on).</ul>"
			+"<ul>Added a simplified version of clothing mod's displacementText/replacementText fields. Look at the bottom of the new 'arcane_amulet.xml' file to see how it works.</ul>"
			+"<ul>Added new 'Major Attribute' primary enchantment modifier for clothing & weapons, with secondary enchantments for energy, aura, physique, arcane, and corruption.</ul>"
			+"<ul>Increased potency costs for 'major drain/drain/minor drain/minor boost/boost/major boost' from 4/2/1/1/2/4 to 8/4/1/1/4/8.</ul>"
			+"<ul>Increased enchantment modifier costs for 'rare/epic/legendary' from '3/5/8' to '4/8/12'.</ul>"
			+"<ul>Added & improved icons for enchantment modifiers.</ul>"
			+"<ul>Added 'chuuni' perk, which gives a large boost to spell power, but also makes your character say embarrassing things when you cast spells.</ul>"
			+"<ul>Added 'brass' and 'midnight green' as colours for clothing/weapons, and 'light purple' as a skin/hair covering colour.</ul>"
			+"<ul>Added support for weapons to count towards sets. (Like the Enforcer set, Maid set, etc.) Look at the katana and wakazashi files to see how they're defined in mods.</ul>"
			+"<ul>Reduced dagger's base value from 5000 to 1500.</ul>"
			+"<ul>You can now only teleport to tiles you've previously visited. (The teleport spell is still only available via the debug menu.)</ul>"
			+"<ul>Tiles you have discovered (by travelling next to them), but haven't visited, are now shaded. (When loading in from a version prior to 0.2.11.5, all discovered tiles will be set as having been visited before.)</ul>"

			+"<li>Bugs:</li>"
			+"<ul>Fixed issue with group oral not working correctly.</ul>"
			+"<ul>Fixed imp attack dialogue not working if you had a companion with you.</ul>"
			+"<ul>Vicky should now correctly stock modded weapons and items with the 'SOLD_BY_VICKY' tag.</ul>"
			+"<ul>Added correct covering detection for all sock and leg clothing, so that cumming onto feet or thighs should now correctly dirty the clothing that's covering those related areas.</ul>"
			+"<ul>Fixed issue where NPCs would start/stop actions in sex. (For what must be the tenth time...)</ul>"
			+"<ul>Fixed bug with the 'Take all' action being used on an NPC's inventory throwing a background error.</ul>"
			+"<ul>Fixed issues with being able to pick up items/weapons/clothing from the floor, even if your inventory was full.</ul>"
			+"<ul>Typo fixes.</ul>"
			+"<ul>Fixed issue with companion's elementals being treated as slaves when in Lilaya's house.</ul>"
			+"<ul>Fixed issue where clothing or weapon tooltip images could sometimes be displayed incorrectly.</ul>"
			+"<ul>Fixed issue with NPC encounter dialogue where the NPC's impregnation fetish check was firing incorrectly.</ul>"
			+"<ul>Fixed initial footjob start text not being shown in sex, as well as the extra tooltip description not working.</ul>"
			+"<ul>Fixed bug where Lilaya's panty scenes involving Rose would all softlock.</ul>"
			+"<ul>Fixed bug in missionary sex where actions to stop penetrations would not be given.</ul>"
			+"<ul>Fixed issue where enchanted clothing without a unique name would sometimes not display the enchantment postfix.</ul>"
			+"<ul>Fixed bug with Arcanist's Hat xml file.</ul>"
			+"<ul>Fixed imp encounters in the central imp tunnels (the dark purple ones) not working correctly.</ul>"
			+"<ul>Fixed background error when starting a group missionary sex scene (which might have been causing the action to start sex to not work).</ul>"
			+"<ul>Weapons that should be pluralised by default are now actually pluralised.</ul>"
			+"<ul>Fixed missionary 'spread legs' action not working.</ul>"
			+"<ul>Fixed issue with modded clothing marked as 'RARE' being given the rarity 'EPIC' in-game.</ul>"
			+"<ul>Fixed issue with wrong character's tail being described in a lot of tail-pegging actions.</ul>"
			+"<ul>Fixed issue with weapons having incorrect determiners.</ul>"
			+"<ul>Fixed bug at end of imp female encounter, where an extra 'endif' would be displayed.</ul>"
		+ "</list>"
	;
	
	public static String disclaimer = "<h6 style='text-align: center; color:"+Colour.GENERIC_ARCANE.toWebHexString()+";'>You must read and agree to the following in order to play this game!</h6>"

			+ "<p>This game is a <b>fictional</b> text-based erotic RPG. All content contained within this game forms part of a fictional universe that is not related to real-life places, people or events.<br/><br/>"

			+ " All of the characters that appear in this story are fictional entities who have given their consent to appear and act in this story."
			+ " If you find yourself concerned for the characters in the story then please be reassured that they are all consenting adults who are speaking lines from a script."
			+ " None of the characters portrayed within this game were or are being harmed in any way during the construction or execution of this game."
			+ " Every character in the game is at least 18 years of age (or is magically the legal age needed to appear in erotic literature in whatever country you are playing this).<br/><br/>"

			+ "By agreeing to this disclaimer and playing this game you agree to be exposed to graphic sexual and adult content that is presented as part of the game's fictional universe."
			+ " Such content consists of, but is not limited to; graphic depictions of sex, inter-species sex (with fantasy creatures), sexual transformation,"
			+ " rape fantasy/implied lack of consent, mild physical violence, sexual violence, and drug use.<br/>"
			+ "Extreme fetish content such as gore/extreme violence, scat, and under/questionable age, is <i>not</i> a part of this game.<br/><br/>"

			+ "By agreeing to this disclaimer and playing this game you also agree that you are <b>at least 18 years of age</b>,"
			+ " or at least the legal age for you to purchase and view pornographic material in your country if that age is over 18.<br/><br/>"

			+ "As a final note, the creators of this game wish to stress that the content presented within is entirely fictional and does not reflect any of their personal views or opinions."
			+ " This game has been made in the spirit of creating a piece of artistic interactive literature, and it is imperative that you maintain a clear distinction between reality and the fictional events depicted in this game.</p>";
	
	public static List<CreditsSlot> credits = new ArrayList<>();

	// World generation:
	public static Generation gen;
	@Override
	public void start(Stage primaryStage) throws Exception {

		CheckForDataDirectory();
		CheckForResFolder();
		
		credits.add(new CreditsSlot("Anonymous", "", 99, 99, 99, 99));

		credits.add(new CreditsSlot("Adhana Konker", "", 0, 0, 3, 0));
		credits.add(new CreditsSlot("Akira", "", 0, 0, 0, 2));
		credits.add(new CreditsSlot("Aleskah", "", 0, 0, 0, 1));
		credits.add(new CreditsSlot("Lexi <3", "", 0, 0, 0, 1));
		credits.add(new CreditsSlot("Alvinsimon", "", 0, 0, 3, 0));
		credits.add(new CreditsSlot("48days", "", 0, 0, 2, 12));
		credits.add(new CreditsSlot("Mylerra", "", 0, 0, 5, 0));
		credits.add(new CreditsSlot("Spaghetti Code", "", 0, 0, 2, 3));
		credits.add(new CreditsSlot("Anonymous_Platypus", "", 0, 0, 1, 0));
		credits.add(new CreditsSlot("Apthydragon", "", 0, 0, 4, 0));
		credits.add(new CreditsSlot("Archan9el S117", "", 0, 0, 0, 7));
		credits.add(new CreditsSlot("SchALLieS", "", 0, 0, 2, 11));
		credits.add(new CreditsSlot("Argmoe", "", 0, 0, 13, 0));
		credits.add(new CreditsSlot("HoneyNutQueerios", "", 0, 0, 11, 0));
		credits.add(new CreditsSlot("Arkhan", "", 0, 0, 6, 0));
		credits.add(new CreditsSlot("Ash", "", 0, 1, 0, 10));
		credits.add(new CreditsSlot("Jack Cloudie", "", 0, 1, 10, 0));
		credits.add(new CreditsSlot("b00marrows", "", 0, 1, 5, 0));
		credits.add(new CreditsSlot("Deimios", "", 0, 0, 3, 5));
		credits.add(new CreditsSlot("Baz GoldenClaw", "", 0, 0, 12, 0));
		credits.add(new CreditsSlot("FidelPinochetov", "", 0, 0, 0, 9));
		credits.add(new CreditsSlot("Tieria", "", 0, 0, 1, 0));
		credits.add(new CreditsSlot("Runehood66", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("Krissy2017", "", 0, 0, 2, 6));
		credits.add(new CreditsSlot("Blackcanine", "", 0, 0, 12, 0));
		credits.add(new CreditsSlot("Blackheart", "", 0, 0, 1, 3));
		credits.add(new CreditsSlot("Blacktouch", "", 0, 0, 2, 12));
		credits.add(new CreditsSlot("BlakLite", "", 0, 0, 4, 0));
		credits.add(new CreditsSlot("Blue999", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("Captain_Sigmus", "", 0, 0, 2, 0));
		credits.add(new CreditsSlot("Brandon Stach", "", 0, 0, 10, 0));
		credits.add(new CreditsSlot("BreakerB", "", 0, 0, 6, 0));
		credits.add(new CreditsSlot("BRobort", "", 0, 0, 9, 0));
		credits.add(new CreditsSlot("BloodsailXXII", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("Burt", "", 0, 0, 6, 0));
		credits.add(new CreditsSlot("Calrak", "", 0, 0, 0, 13));
		credits.add(new CreditsSlot("CancerMage", "", 0, 0, 7, 0));
		credits.add(new CreditsSlot("Casper &quot;Cdaser&quot; D.", "", 0, 0, 10, 0));
		credits.add(new CreditsSlot("CelestialNightmare", "", 0, 0, 0, 13));
		credits.add(new CreditsSlot("Sxythe", "", 0, 0, 0, 2));
		credits.add(new CreditsSlot("Lexi the slut", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("Chattyneko", "", 0, 0, 5, 0));
		credits.add(new CreditsSlot("Vmpireassassin (Chloe)", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("cinless", "", 0, 0, 0, 8));
		credits.add(new CreditsSlot("crashtestdummy", "", 0, 0, 9, 5));
		credits.add(new CreditsSlot("Crimson", "", 0, 0, 0, 12));
		credits.add(new CreditsSlot("CrowCorvus", "", 0, 0, 4, 0));
		credits.add(new CreditsSlot("Cryostorm", "", 0, 0, 10, 0));
		credits.add(new CreditsSlot("Cursed Rena", "", 0, 0, 1, 12));
		credits.add(new CreditsSlot("Cynical-Cy", "", 0, 0, 6, 0));
		credits.add(new CreditsSlot("Dace617", "", 0, 0, 0, 4));
		credits.add(new CreditsSlot("Saladofstones", "", 0, 0, 7, 0));
		credits.add(new CreditsSlot("Dan", "", 0, 1, 0, 10));
		credits.add(new CreditsSlot("Daniel D Magnan", "", 0, 0, 5, 0));
		credits.add(new CreditsSlot("Darthsawyer", "", 0, 0, 1, 0));
		credits.add(new CreditsSlot("Yllarius", "", 0, 0, 2, 0));
		credits.add(new CreditsSlot("DeadEyesSee", "", 0, 0, 6, 0));
		credits.add(new CreditsSlot("DeadMasterZero", "", 0, 0, 8, 1));
		credits.add(new CreditsSlot("Demonicgamer666", "", 0, 0, 0, 8));
		credits.add(new CreditsSlot("John Scarlet", "", 0, 0, 0, 1));
		credits.add(new CreditsSlot("Desgax", "", 0, 0, 7, 0));
		credits.add(new CreditsSlot("Destont", "", 0, 0, 11, 0));
		credits.add(new CreditsSlot("rinoskin", "", 0, 0, 0, 4));
		credits.add(new CreditsSlot("suka", "", 0, 0, 14, 0));
		credits.add(new CreditsSlot("Alatar", "", 0, 0, 0, 2));
		credits.add(new CreditsSlot("Elmsdor", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("EnigmaticYoshi", "", 0, 0, 14, 0));
		credits.add(new CreditsSlot("Endless", "", 0, 0, 5, 2));
		credits.add(new CreditsSlot("Gr33n B3ans", "", 0, 0, 0, 2));
		credits.add(new CreditsSlot("Erin Kyan", "", 0, 0, 8, 0));
		credits.add(new CreditsSlot("Avandemine", "", 0, 0, 1, 6));
		credits.add(new CreditsSlot("F. Rowan", "", 0, 0, 7, 0));
		credits.add(new CreditsSlot("Farseeker", "", 0, 0, 8, 0));
		credits.add(new CreditsSlot("pupslut felix", "", 0, 0, 0, 12));
		credits.add(new CreditsSlot("Fenrakk101", "", 0, 0, 11, 0));
		credits.add(new CreditsSlot("Fiona", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("ForeverFree2MeTaMax", "", 0, 0, 14, 0));
		credits.add(new CreditsSlot("FossorTumulus", "", 0, 0, 6, 0));
		credits.add(new CreditsSlot("Freekingamer", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("fun_bot", "", 0, 0, 0, 3));
		credits.add(new CreditsSlot("Niki Parks", "", 0, 0, 14, 0));
		credits.add(new CreditsSlot("Garkylal", "", 0, 0, 5, 0));
		credits.add(new CreditsSlot("Georgio154", "", 0, 0, 1, 6));
		credits.add(new CreditsSlot("glocknar", "", 0, 0, 11, 0));
		credits.add(new CreditsSlot("Goldmember", "", 0, 0, 0, 3));
		credits.add(new CreditsSlot("Grakcnar", "", 0, 0, 9, 0));
		credits.add(new CreditsSlot("WodashGSJ", "", 0, 0, 10, 0));
		credits.add(new CreditsSlot("Aceofspades", "", 0, 0, 2, 0));
		credits.add(new CreditsSlot("Assiyalos", "", 0, 0, 4, 0));
		credits.add(new CreditsSlot("Hedgehog", "", 0, 0, 0, 6));
		credits.add(new CreditsSlot("Helyriel", "", 0, 0, 10, 0));
		credits.add(new CreditsSlot("Jatch", "", 0, 0, 6, 0));
		credits.add(new CreditsSlot("Bocaj91", "", 0, 0, 0, 12));
		credits.add(new CreditsSlot("Krejil", "", 0, 0, 9, 0));
		credits.add(new CreditsSlot("Eushully", "", 0, 0, 0, 8));
		credits.add(new CreditsSlot("Garth614", "", 0, 0, 0, 10));
		credits.add(new CreditsSlot("HerrKommissar11", "", 0, 0, 1, 4));
		credits.add(new CreditsSlot("Kaerea", "", 0, 0, 3, 0));
		credits.add(new CreditsSlot("Tappi", "", 0, 0, 2, 0));
		credits.add(new CreditsSlot("Kaleb the Wise", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("Karlimero", "", 0, 0, 0, 3));
		credits.add(new CreditsSlot("KingofKings", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("KazukiNero", "", 0, 0, 7, 0));
		credits.add(new CreditsSlot("Kelly999", "", 0, 1, 10, 0));
		credits.add(new CreditsSlot("kenshin5491", "", 0, 0, 13, 0));
		credits.add(new CreditsSlot("Kestrel", "", 0, 0, 14, 0));
		credits.add(new CreditsSlot("Kiroberos", "", 0, 0, 0, 12));
		credits.add(new CreditsSlot("Kernog", "", 0, 0, 1, 0));
		credits.add(new CreditsSlot("Knight-Lord Xander", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("Chris Turpin", "", 0, 0, 12, 0));
		credits.add(new CreditsSlot("Lee Thompson", "", 0, 0, 9, 0));
		credits.add(new CreditsSlot("Leob", "", 0, 0, 10, 4));
		credits.add(new CreditsSlot("Pallid", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("ilderon", "", 0, 0, 5, 0));
		credits.add(new CreditsSlot("Littlemankitten", "", 0, 0, 0, 7));
		credits.add(new CreditsSlot("Mr L", "", 0, 0, 4, 1));
		credits.add(new CreditsSlot("loveless", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("Vaddex", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("Kitsune Lyn", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("matchsticks", "", 0, 0, 5, 0));
		credits.add(new CreditsSlot("masterpuppet", "", 0, 0, 10, 0));
		credits.add(new CreditsSlot("Nightmare", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("AlphaOneBravo", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("Max Nobody", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("Neximus", "", 0, 0, 5, 0));
		credits.add(new CreditsSlot("Mora", "", 0, 0, 4, 0));
		credits.add(new CreditsSlot("Muhaku", "", 0, 0, 9, 0));
		credits.add(new CreditsSlot("Kobu", "", 0, 0, 0, 9));
		credits.add(new CreditsSlot("IreCobra", "", 0, 0, 5, 0));
		credits.add(new CreditsSlot("NeonRaven94", "", 0, 0, 0, 4));
		credits.add(new CreditsSlot("Nick LaBlue", "", 0, 0, 13, 0));
		credits.add(new CreditsSlot("Kvernik", "", 0, 0, 6, 0));
		credits.add(new CreditsSlot("Niko", "", 0, 0, 14, 0));
		credits.add(new CreditsSlot("Nnxx", "", 0, 1, 3, 2));
		credits.add(new CreditsSlot("NorwegianMonster", "", 0, 0, 0, 6));
		credits.add(new CreditsSlot("Seo Leifthrasir", "", 0, 0, 0, 1));
		credits.add(new CreditsSlot("Odd8Ball", "", 0, 0, 0, 13));
		credits.add(new CreditsSlot("Party Commissar", "", 0, 0, 4, 8));
		credits.add(new CreditsSlot("Rohsie", "", 0, 0, 0, 10));
		credits.add(new CreditsSlot("P.", "", 0, 0, 0, 4));
		credits.add(new CreditsSlot("BLKCandy", "", 0, 0, 10, 0));
		credits.add(new CreditsSlot("Pierre Mura", "", 0, 0, 0, 11));
		credits.add(new CreditsSlot("Pokys", "", 0, 0, 9, 0));
		credits.add(new CreditsSlot("QQQ", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("Rakesh", "", 0, 0, 8, 0));
		credits.add(new CreditsSlot("R.W", "", 0, 3, 6, 0));
		credits.add(new CreditsSlot("The Void Prince", "", 0, 0, 8, 0));
		credits.add(new CreditsSlot("Master's dumb bitch", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("Reila Oda", "", 0, 0, 0, 7));
		credits.add(new CreditsSlot("Roarik", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("Dark_Lord", "", 0, 0, 0, 6));
		credits.add(new CreditsSlot("redwulfen", "", 0, 0, 0, 13));
		credits.add(new CreditsSlot("Roger Reyne", "", 0, 0, 0, 3));
		credits.add(new CreditsSlot("RogueRandom", "", 0, 0, 11, 0));
		credits.add(new CreditsSlot("Horagen81", "", 0, 0, 0, 13));
		credits.add(new CreditsSlot("RyubosJ", "", 0, 0, 6, 0));
		credits.add(new CreditsSlot("Saladine the Legendary", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("Sand9k", "", 0, 0, 0, 6));
		credits.add(new CreditsSlot("Schande", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("Blue Kobold", "", 0, 0, 7, 0));
		credits.add(new CreditsSlot("sebasjac", "", 0, 0, 0, 2));
		credits.add(new CreditsSlot("S", "", 0, 0, 1, 12));
		credits.add(new CreditsSlot("Shas'O Dal'yth Kauyon Kais Taku", "", 0, 0, 14, 0));
		credits.add(new CreditsSlot("Crow Invictus", "", 0, 0, 13, 0));
		credits.add(new CreditsSlot("Sheltem", "", 0, 0, 12, 0));
		credits.add(new CreditsSlot("shrikes", "", 0, 0, 5, 0));
		credits.add(new CreditsSlot("Sig", "", 0, 0, 4, 0));
		credits.add(new CreditsSlot("Silentark", "", 0, 0, 11, 0));
		credits.add(new CreditsSlot("Sir beans", "", 0, 0, 5, 0));
		credits.add(new CreditsSlot("Sorter", "", 0, 0, 0, 9));
		credits.add(new CreditsSlot("Spectacular", "", 0, 0, 6, 0));
		credits.add(new CreditsSlot("Spookermen", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("Starchiller", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("Steph", "", 0, 0, 4, 0));
		credits.add(new CreditsSlot("Strigon888", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("Suvarestin", "", 0, 0, 2, 0));
		credits.add(new CreditsSlot("Swift Shot", "", 0, 0, 12, 0));
		credits.add(new CreditsSlot("TalonMort", "", 0, 0, 12, 0));
		credits.add(new CreditsSlot("Tanall", "", 0, 1, 12, 0));
		credits.add(new CreditsSlot("Tanner D.", "", 0, 0, 0, 6));
		credits.add(new CreditsSlot("Terrance", "", 0, 0, 3, 0));
		credits.add(new CreditsSlot("Testostetyrone", "", 0, 0, 10, 0));
		credits.add(new CreditsSlot("The Brocenary", "", 0, 0, 0, 1));
		credits.add(new CreditsSlot("Jordan Aitken", "", 0, 0, 14, 0));
		credits.add(new CreditsSlot("T. Garou", "", 0, 0, 0, 12));
		credits.add(new CreditsSlot("xerton", "", 0, 0, 3, 0));
		credits.add(new CreditsSlot("Timmybond24", "", 0, 0, 0, 4));
		credits.add(new CreditsSlot("TKaempfer", "", 0, 0, 3, 0));
		credits.add(new CreditsSlot("FreakyHydra", "", 0, 0, 0, 4));
		credits.add(new CreditsSlot("Kahvi_Toope", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("Torinir", "", 0, 0, 13, 0));
		credits.add(new CreditsSlot("Torsten015", "", 0, 0, 0, 13));
		credits.add(new CreditsSlot("TreenVall", "", 0, 0, 3, 0));
		credits.add(new CreditsSlot("triangleman", "", 0, 0, 14, 0));
		credits.add(new CreditsSlot("Antriad", "", 0, 0, 1, 8));
		credits.add(new CreditsSlot("Isidoros", "", 0, 0, 7, 0));
		credits.add(new CreditsSlot("SolarEidolon", "", 0, 0, 3, 0));
		credits.add(new CreditsSlot("Vaelin", "", 0, 0, 4, 9));
		credits.add(new CreditsSlot("vasadariu", "", 0, 0, 10, 0));
		credits.add(new CreditsSlot("Venomy", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("waaaghkus", "", 0, 0, 14, 0));
		credits.add(new CreditsSlot("iloveyouMiaoNiNi", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("Weegschaal", "", 0, 0, 0, 3));
		credits.add(new CreditsSlot("Whatever", "", 0, 0, 12, 0));
		credits.add(new CreditsSlot("William Brown", "", 0, 0, 5, 2));
		credits.add(new CreditsSlot("Drahin", "", 0, 0, 0, 5));
		credits.add(new CreditsSlot("CMPirate9867", "", 0, 0, 8, 0));
		credits.add(new CreditsSlot("Wolfrave", "", 0, 0, 2, 0));
		credits.add(new CreditsSlot("Wolfregis", "", 0, 0, 0, 14));
		credits.add(new CreditsSlot("Nelson Adams", "", 0, 0, 12, 0));
		credits.add(new CreditsSlot("Zakarin", "", 0, 0, 0, 9));
		
		
		credits.sort(Comparator.comparing((CreditsSlot a) -> a.getName().toLowerCase()));
		
		
		Main.primaryStage = primaryStage;
		
		Main.primaryStage.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				if(t) {
					TooltipUpdateThread.cancelThreads = true;
				}
			}
		});

		Main.primaryStage.getIcons().add(WINDOW_IMAGE);

		Main.primaryStage.setTitle("Lilith's Throne " + VERSION_NUMBER + " " + VERSION_DESCRIPTION+(DEBUG?" (Debug Mode)":""));
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lilithsthrone/res/fxml/main.fxml"));

		Pane pane = loader.load();

		mainScene = new Scene(pane);

		if (properties.hasValue(PropertyValue.lightTheme)) {
			mainScene.getStylesheets().add("/com/lilithsthrone/res/css/stylesheet_light.css");
		} else {
			mainScene.getStylesheets().add("/com/lilithsthrone/res/css/stylesheet.css");
		}

		mainController = loader.getController();
		Main.primaryStage.setScene(mainScene);
		Main.primaryStage.show();
		Main.game = new Game();
		Main.sexEngine = new Sex();
		
		loader = new FXMLLoader(getClass().getResource("/com/lilithsthrone/res/fxml/main.fxml"));
		try {
			if (Main.mainScene == null) {
				pane = loader.load();
				Main.mainController = loader.getController();

				Main.mainScene = new Scene(pane);
				if (Main.getProperties().hasValue(PropertyValue.lightTheme))
					Main.mainScene.getStylesheets().add("/com/lilithsthrone/res/css/stylesheet_light.css");
				else
					Main.mainScene.getStylesheets().add("/com/lilithsthrone/res/css/stylesheet.css");
			}

			Main.primaryStage.setScene(Main.mainScene);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Main.game.setContent(new Response("", "", OptionsDialogue.MENU));
		
	}
	
	protected static void CheckForDataDirectory() {
		File dir = new File("data/");
		if(!dir.exists()) {
			Alert a = new Alert(AlertType.ERROR, "Unable to find the 'data' folder. Saving and error logging is disabled.\nMake sure that you've extracted the game from the zip file, and that the file has write permissions.\nContinue?",
					ButtonType.YES, ButtonType.NO);
			a.showAndWait().ifPresent(response -> {
			     if (response == ButtonType.NO) {
			         System.exit(1);
			     }
			 });
		}
	}
	
	protected static void CheckForResFolder() {
		File dir = new File("res/");
		if(!dir.exists()) {
			Alert a = new Alert(AlertType.WARNING, "Could not find the 'res' folder. This might cause errors and present sections of missing text.\nContinue?", ButtonType.YES, ButtonType.NO);
			a.showAndWait().ifPresent(response -> {
				if(response == ButtonType.NO)
				{
					System.exit(1);
				}
			});
		}
	}

	public static void main(String[] args) {
		
		// Create folders:
		File dir = new File("data/");
		dir.mkdir();
		dir = new File("data/saves");
		dir.mkdir();
		dir = new File("data/characters");
		dir.mkdir();
		
		// Open error log
		if(!DEBUG) {
			try {
				@SuppressWarnings("resource")
				PrintStream stream = new PrintStream("data/error.log");
				System.setErr(stream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		// Load properties:
		if (new File("data/properties.xml").exists()) {
			try {
				properties = new Properties();
				properties.loadPropertiesFromXML();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			properties = new Properties();
			properties.savePropertiesAsXML();
		}
		
		launch(args);
	}
	
	/**
	 * Starts a completely new game. Runs a new World Generation.
	 */
	public static void startNewGame(DialogueNodeOld startingDialogueNode) {
		
		Main.game = new Game();
		
		// Generate world:
		if (!(gen == null))
			if (gen.isRunning()) {
				gen.cancel();
			}

		gen = new Generation();

		gen.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/lilithsthrone/res/fxml/main.fxml"));
				Pane pane;
				try {
					if (Main.mainScene == null) {
						pane = loader.load();
						Main.mainController = loader.getController();

						Main.mainScene = new Scene(pane);
						if (Main.getProperties().hasValue(PropertyValue.lightTheme))
							Main.mainScene.getStylesheets().add("/com/lilithsthrone/res/css/stylesheet_light.css");
						else
							Main.mainScene.getStylesheets().add("/com/lilithsthrone/res/css/stylesheet.css");
					}

					Main.primaryStage.setScene(Main.mainScene);

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Main.game.setPlayer(new PlayerCharacter(new NameTriplet("Player"), 1, null, Gender.M_P_MALE, Subspecies.HUMAN, RaceStage.HUMAN, null, WorldType.EMPTY, PlaceType.GENERIC_MUSEUM));

				Main.game.initNewGame(startingDialogueNode);

				Main.game.endTurn(0);
				//Main.mainController.processNewDialogue();
			}
		});
		new Thread(gen).start();
	}
	
	public static boolean isVersionOlderThan(String versionToCheck, String versionToCheckAgainst) {
		String[] v1 = versionToCheck.split("\\.");
		String[] v2 = versionToCheckAgainst.split("\\.");
		
		try {
			int maxlength = (v1.length > v2.length) ? v1.length : v2.length;
			for (int i = 0; i < maxlength; i++) {
				int v1i;
				int v2i;
				
				if(v1[1].charAt(0)=='1') { // Versions prior to 0.2.x used an old system of the format: 0.1.10.1 being a lower version than 0.1.9.1:
					v1i = (i < v1.length) ? Integer.valueOf((v1[i]+"00").substring(0, 3)) : 0;
					v2i = (i < v2.length) ? Integer.valueOf((v2[i]+"00").substring(0, 3)) : 0;
					
				} else { // Versions of 0.2.x and higher use a new system of the format: 0.2.10.1 being a higher version than 0.2.9.1:
					v1i = (i < v1.length) ? Integer.valueOf(v1[i]) : 0;
					v2i = (i < v2.length) ? Integer.valueOf(v2[i]) : 0;
				}
				
				if (v1i < v2i) {
					return true;
				} else if (v1i > v2i) {
					return false;
				} 
			}
			
		} catch(Exception ex) {
			return true;
		}
		
		return false;
	}
	
	public static int getFontSize() {
		return properties.fontSize;
	}

	public static void setFontSize(int size) {
		properties.fontSize = size;
		properties.savePropertiesAsXML();
	}
	
	
	public static void quickSaveGame() {
		if (Main.game.isInCombat()) {
			Main.game.flashMessage(Colour.GENERIC_BAD, "Cannot quicksave while in combat!");
			
		} else if (Main.game.isInSex()) {
			Main.game.flashMessage(Colour.GENERIC_BAD, "Cannot quicksave while in sex!");
			
		} else if (Main.game.getCurrentDialogueNode().getDialogueNodeType()!=DialogueNodeType.NORMAL) {
			Main.game.flashMessage(Colour.GENERIC_BAD, "Can only quicksave in a normal scene!");
			
		} else if (!Main.game.isStarted() || !Main.game.getCurrentDialogueNode().equals(Main.game.getDefaultDialogueNoEncounter())) {
			Main.game.flashMessage(Colour.GENERIC_BAD, "Cannot save in this scene!");
			
		} else {
			Main.getProperties().lastQuickSaveName = "QuickSave_"+Main.game.getPlayer().getName();
			saveGame("QuickSave_"+Main.game.getPlayer().getName(), true);
		}
	}

	public static void quickLoadGame() {
		String name = "QuickSave_"+Main.game.getPlayer().getName();
		
//		if(new File("data/saves/"+name+".lts").exists()) {
			loadGame(name);
//		} else {
//			loadGame(Main.getProperties().lastQuickSaveName);
//		}
	}

	public static boolean isSaveGameAvailable() {
		return Main.game.isStarted() && Main.game.getSavedDialogueNode() == Main.game.getDefaultDialogueNoEncounter();
	}
	
	public static void saveGame(String name, boolean allowOverwrite) {
		if (name.length()==0) {
			Main.game.flashMessage(Colour.GENERIC_BAD, "Name too short!");
			return;
		}
		if (name.length() > 32) {
			Main.game.flashMessage(Colour.GENERIC_BAD, "Name too long!");
			return;
		}
		if (!name.matches("[a-zA-Z0-9]+[a-zA-Z0-9' _]*")) {
			Main.game.flashMessage(Colour.GENERIC_BAD, "Incompatible characters!");
			return;
		}
		
		Game.exportGame(name, allowOverwrite);

		try {
			properties.lastSaveLocation = name;//"data/saves/"+name+".lts";
			properties.nameColour = Femininity.valueOf(game.getPlayer().getFemininityValue()).getColour().toWebHexString();
			properties.name = game.getPlayer().getName();
			properties.level = game.getPlayer().getLevel();
			properties.money = game.getPlayer().getMoney();
			properties.arcaneEssences = game.getPlayer().getEssenceCount(TFEssence.ARCANE);
			properties.race = game.getPlayer().getSubspecies().getName(game.getPlayer());
			properties.quest = game.getPlayer().getQuest(QuestLine.MAIN).getName();

			properties.savePropertiesAsXML();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean isLoadGameAvailable(String name) {
		File file = new File("data/saves/"+name+".xml");

		return file.exists();
	}
	
	public static void loadGame(String name) {
		if (isLoadGameAvailable(name)) {
			Game.importGame(name);
		}
	}
	
	public static void deleteGame(String name) {
		File file = new File("data/saves/"+name+".xml");

		if (file.exists()) {
			try {
				file.delete();
				Main.game.setContent(new Response("", "", Main.game.getCurrentDialogueNode()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		} else {
			Main.game.flashMessage(Colour.GENERIC_BAD, "File not found...");
		}
	}
	
	public static void deleteExportedGame(String name) {
		File file = new File("data/saves/"+name+".xml");

		if (file.exists()) {
			try {
				file.delete();
				Main.game.setContent(new Response("", "", Main.game.getCurrentDialogueNode()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		} else {
			Main.game.flashMessage(Colour.GENERIC_BAD, "File not found...");
		}
	}
	
	public static void deleteExportedCharacter(String name) {
		File file = new File("data/characters/"+name+".xml");

		if (file.exists()) {
			try {
				file.delete();
				Main.game.setContent(new Response("", "", Main.game.getCurrentDialogueNode()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		} else {
			Main.game.flashMessage(Colour.GENERIC_BAD, "File not found...");
		}
	}
	
	public static List<File> getSavedGames() {
		List<File> filesList = new ArrayList<>();
		
		File dir = new File("data/saves");
		if (dir.isDirectory()) {
			File[] directoryListing = dir.listFiles((path, name) -> name.endsWith(".xml"));
			if (directoryListing != null) {
				filesList.addAll(Arrays.asList(directoryListing));
			}
		}

		filesList.sort(Comparator.comparingLong(File::lastModified).reversed());
		
		return filesList;
	}
	
	public static List<File> getCharactersForImport() {
		List<File> filesList = new ArrayList<>();
		
		File dir = new File("data/characters");
		if (dir.isDirectory()) {
			File[] directoryListing = dir.listFiles((path, name) -> name.endsWith(".xml"));
			if (directoryListing != null) {
				filesList.addAll(Arrays.asList(directoryListing));
			}
		}

		filesList.sort(Comparator.comparingLong(File::lastModified).reversed());
		
		return filesList;
	}
	
	public static List<File> getSlavesForImport() {
		List<File> filesList = new ArrayList<>();
		
		File dir = new File("data/characters");
		if (dir.isDirectory()) {
			File[] directoryListing = dir.listFiles((path, name) -> name.endsWith(".xml"));
			if (directoryListing != null) {
				filesList.addAll(Arrays.asList(directoryListing));
			}
		}
		
		filesList.sort(Comparator.comparingLong(File::lastModified).reversed());
		
		return filesList;
	}
	
	public static List<File> getGamesForImport() {
		List<File> filesList = new ArrayList<>();
		
		File dir = new File("data/saves");
		if (dir.isDirectory()) {
			File[] directoryListing = dir.listFiles((path, name) -> name.endsWith(".xml"));
			if (directoryListing != null) {
				filesList.addAll(Arrays.asList(directoryListing));
			}
		}

		filesList.sort(Comparator.comparingLong(File::lastModified).reversed());
		
		return filesList;
	}
	
	public static void importCharacter(File file) {
		if (file != null) {
			try {
				Main.game.setPlayer(CharacterUtils.startLoadingCharacterFromXML());
				Main.game.setPlayer(CharacterUtils.loadCharacterFromXML(file, Main.game.getPlayer(),
						CharacterImportSetting.NO_PREGNANCY,
						CharacterImportSetting.NO_COMPANIONS,
						CharacterImportSetting.NO_ELEMENTAL,
						CharacterImportSetting.CLEAR_SLAVERY));
				
				Main.game.getPlayer().getSlavesOwned().clear();
				Main.game.getPlayer().endPregnancy(false);
				
				Main.game.setRenderAttributesSection(true);
				Main.game.clearTextStartStringBuilder();
				Main.game.clearTextEndStringBuilder();
				Main.getProperties().setValue(PropertyValue.newWeaponDiscovered, false);
				Main.getProperties().setValue(PropertyValue.newClothingDiscovered, false);
				Main.getProperties().setValue(PropertyValue.newItemDiscovered, false);
				Main.game.getPlayer().calculateStatusEffects(0);

				Main.game.initNewGame(CharacterCreation.START_GAME_WITH_IMPORT);
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static Properties getProperties() {
		return properties;
	}

	public static void saveProperties() {
		properties.savePropertiesAsXML();
	}
}
