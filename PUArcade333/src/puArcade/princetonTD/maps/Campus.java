package puArcade.princetonTD.maps;

import puArcade.princetonTD.game.Game;
import puArcade.princetonTD.game.Mode;
import puArcade.princetonTD.players.PlayerLocation;
import puArcade.princetonTD.players.Team;
import android.graphics.Color;
import android.graphics.Rect;

public class Campus extends Map
{
	public final static String BG_IMAGE;
	public final static String NAME = "mapcampus";

	static
	{
		BG_IMAGE = "drawable/mapcampus";
	}

	public Campus (Game game) 
	{
		super(  game,
				828,  // width
				445,  // height
				100,  // gold
				20,   // lives
				0,    // x offset
				0,    // y offset
				828,  // grid width
				445,  // grid height
				Mode.MODE_SOLO, // mode
				BG_IMAGE, // background image
				NAME  // name
				);

		opacity = 0.f;

		Team e = new Team(1,"Default team",Color.BLACK);
		e.addStartZone(new Rect(0, 410, 300, 445));
		e.setEndZone(new Rect(683, 78, 766, 134));
		e.addPlayerLocation(new PlayerLocation(1,new Rect(0,0,828,445)));
		teams.add(e);

		addWall(new Rect(0, 0, 247, 191));
		addWall(new Rect(86, 316, 212, 390));
		addWall(new Rect(248, 232, 293, 410));
		addWall(new Rect(406, 0, 514, 148));
		addWall(new Rect(560, 0, 766, 78));
		addWall(new Rect(535, 180, 657, 243));
		addWall(new Rect(674, 215, 772, 271));
		addWall(new Rect(702, 306, 762, 400));
		addWall(new Rect(587, 337, 643, 390));
		addWall(new Rect(338, 204, 500, 250));
		addWall(new Rect(469, 250, 500, 288));
		addWall(new Rect(354, 344, 430, 368));
		addWall(new Rect(409, 312, 430, 344));
		addWall(new Rect(480, 352, 555, 386));
		addWall(new Rect(504, 331, 530, 400));
	}
}
