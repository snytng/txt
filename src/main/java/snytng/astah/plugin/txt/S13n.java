package snytng.astah.plugin.txt;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;

public class S13n
{
	public S13n() {
		// no action
	}

	enum Direction {
		RIGHTWARD(
				Comparator.comparing(Point2D::getX)
					.thenComparing(Point2D::getY)
					),
		DOWNWARD(
				Comparator.comparing(Point2D::getY)
					.thenComparing(Point2D::getX)
					),
		LEFTWARD(
				Comparator.comparing(Point2D::getX, Comparator.reverseOrder())
					.thenComparing(Point2D::getY, Comparator.reverseOrder())
					),
		UPWARD(
				Comparator.comparing(Point2D::getY, Comparator.reverseOrder())
					.thenComparing(Point2D::getX, Comparator.reverseOrder())
					);

		private final Comparator<Point2D> comparator;
		private Direction(Comparator<Point2D> c){
			this.comparator = c;
		}
	}

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


	final private static List<String> supportedPresentations = new ArrayList<String>() {
		{
			add("Topic"); // マインドマップのトピック
			//add("Frame"); // 図のフレーム
		}
	};

	public static String getStrings(IPresentation[] ps, Direction direction) {
		Map<Point2D, String> pMap = new HashMap<>();

		for(IPresentation p : ps) {
			// Presentationにモデルがない、かつ、サポートするタイプ出ない場合にはスキップ
			if(p.getModel() == null) {
				System.out.println("Presentation has no model.type is " + p.getType());
				if(!supportedPresentations.contains(p.getType())) {
					continue;
				}
			}

			if(p instanceof INodePresentation) {
				INodePresentation node = (INodePresentation)p;
				Point2D point = node.getLocation();
				String  text  = node.getLabel();
				if(text != null && ! text.isEmpty()) {
					pMap.put(point, text);
				}
			}
			else if(p instanceof ILinkPresentation) {
				ILinkPresentation link = (ILinkPresentation)p;
				String x = link.getProperty("name.point.x");
				String y = link.getProperty("name.point.y");
				String text = link.getLabel();
				if(text != null && ! text.isEmpty()) {
					Point2D point = new Point2D.Double(Double.parseDouble(x), Double.parseDouble(y));
					pMap.put(point,  text);
				}
			}
		}

		Point2D[] keys = pMap.keySet().toArray(new Point2D[pMap.keySet().size()]);

		Arrays.sort(keys, direction.comparator);

		StringBuilder sb = new StringBuilder();
		for(Point2D key : keys) {
			sb.append(pMap.get(key));
			sb.append(System.lineSeparator());
		}

		return sb.toString();

	}

}
