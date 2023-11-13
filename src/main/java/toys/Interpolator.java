package toys;

import java.util.List;
import java.util.ListIterator;

public abstract class Interpolator {

	interface NeighbouringPoints {
		public Point getPreviousPoint(Point point);
		public Point getNextPoint(Point point);
	}

	private enum Direction {
		up,
		down
	};

	public Curve interpolate(Curve curve, List<Point> pivotValues) {
		if (curve.hasPoints()) {
			final List<Point> points = curve.getPoints();
			final int mid = points.size() / 2;
			final int last = points.size() - 1;
			final int[] pointIntervals = new int[points.size()];
			int i = 0;
			for (Point point : points) {
				pointIntervals[i++] = point.getInterval();
			}

			final NeighbouringPoints neighbours = new NeighbouringPoints() {
				private int finish(int pos, Direction direction) {
					int ind = 0;
					if (direction == Direction.down) {
						if (pos == 0) {
							ind = -1;
						} else {
							ind = pos - 1;
						}
					} else {
						if (pos == last) {
							ind = last + 1;
						} else {
							ind = pos + 1;
						}
					}
					return ind;
				}
				
				private int descend(int pos, int arrayInterval, int pointInterval, Direction direction) {
					int ind = 0;
					int thePointsInterval = pointIntervals[pos];
					int newArrayInterval = arrayInterval / 2 + arrayInterval % 2;
					boolean toContinue = (arrayInterval > 1);
					if (pointInterval == thePointsInterval) {
						ind = finish(pos, direction);
					} else if (thePointsInterval > pointInterval) {
						if (toContinue) {
							if (pos - newArrayInterval < 0) {
								newArrayInterval = pos;
							}
							ind = descend(pos - newArrayInterval, newArrayInterval, pointInterval, direction);
						} else {
							if (direction == Direction.down) {
								ind = pos - 1;
							} else {
								ind = pos;
							}
						}
					} else if (thePointsInterval < pointInterval) {
						if (toContinue) {
							if (pos + newArrayInterval > last) {
								newArrayInterval--;
							}
							ind = descend(pos + newArrayInterval, newArrayInterval, pointInterval, direction);
						} else {
							if (direction == Direction.down) {
								ind = pos;
							} else {
								ind = pos + 1;
							}
						}
					}
					return ind;
				}
				
				public Point getPreviousPoint(Point point) {
					Point previous = null;
					int ind = descend(mid, mid, point.getInterval(), Direction.down);
					if (ind >= 0) {
						previous = points.get(ind);
					}
					return previous;
				}
				
				public Point getNextPoint(Point point) {
					Point next = null;
					int ind = descend(mid, mid, point.getInterval(), Direction.up);
					if (ind < points.size()) {
						next = points.get(ind);
					}
					return next;
				}
			};

			ListIterator<Point> pivots = pivotValues.listIterator(pivotValues.size());
			while (pivots.hasPrevious()) {
				Point current = pivots.previous();
				if (curve.getPoint(current) != null) {
					continue;
				}
				Point previous = neighbours.getPreviousPoint(current);
				Point next = neighbours.getNextPoint(current);
				Point interpolated = createInterpolatedPoint(previous, next, current);
				curve.addPoint(interpolated);
			}
			// Collactions.sort(points);
		}
		return curve;
	}

	public abstract Point createInterpolatedPoint(Point previous, Point next, Point pivot);
}
