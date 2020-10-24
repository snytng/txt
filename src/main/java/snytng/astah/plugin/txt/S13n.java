package snytng.astah.plugin.txt;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;

public class S13n
{
	public S13n() {
	}

	enum Direction {RIGHTWARD, DOWNWARD, LEFTWARD, UPWARD};

	public static String getStrings(IDiagram diagram, Direction direction) {
		System.out.println("## " + diagram.getName());

		IPresentation[] ps = new IPresentation[0];
		try {
			ps = diagram.getPresentations();
		} catch (InvalidUsingException e) {
			e.printStackTrace();
		}

		return getStrings(ps, direction);
	}

	public static String getStrings(IPresentation[] ps, Direction direction) {
		Map<Point2D, String> pMap = new HashMap<>();

		for(IPresentation p : ps) {
			if(p.getModel() == null) { // フレーム？
				continue;
			}

			if(p instanceof INodePresentation) {
				INodePresentation node = (INodePresentation)p;
				Point2D point = node.getLocation();
				String  text  = node.getLabel();
				if(text != null && ! text.isEmpty()) {
					pMap.put(point, text);
				}
			}
		}

		Point2D[] keys = pMap.keySet().toArray(new Point2D[pMap.keySet().size()]);

		switch(direction) {
		case RIGHTWARD:
			Arrays.sort(keys, Comparator.comparing(Point2D::getX)
					.thenComparing(Point2D::getY));
			break;
		case DOWNWARD:
			Arrays.sort(keys, Comparator.comparing(Point2D::getY)
					.thenComparing(Point2D::getX));
			break;
		case LEFTWARD:
			Arrays.sort(keys, Comparator.comparing(
					Point2D::getX, Comparator.reverseOrder())
					.thenComparing(Point2D::getY, Comparator.reverseOrder()));
			break;
		case UPWARD:
			Arrays.sort(keys, Comparator.comparing(
					Point2D::getY, Comparator.reverseOrder())
					.thenComparing(Point2D::getX, Comparator.reverseOrder()));
			break;
		default:
			break;
		}

		StringBuilder sb = new StringBuilder();
		for(Point2D key : keys) {
			sb.append(pMap.get(key));
			sb.append(System.lineSeparator());
		}

		return sb.toString();

	}

}
