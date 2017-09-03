package org.maripo.josm.movementalert;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Set;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSelectionListener;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.event.AbstractDatasetChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataChangedEvent;
import org.openstreetmap.josm.data.osm.event.DataSetListener;
import org.openstreetmap.josm.data.osm.event.NodeMovedEvent;
import org.openstreetmap.josm.data.osm.event.PrimitivesAddedEvent;
import org.openstreetmap.josm.data.osm.event.PrimitivesRemovedEvent;
import org.openstreetmap.josm.data.osm.event.RelationMembersChangedEvent;
import org.openstreetmap.josm.data.osm.event.TagsChangedEvent;
import org.openstreetmap.josm.data.osm.event.WayNodesChangedEvent;

class MovementMonitor implements DataSetListener, AWTEventListener, DataSelectionListener {
	
	private boolean isDragging = false;
	
	interface MovementListener {
		public void onMove (LatLon from, LatLon to);
	}
	MovementListener movementListener = null;
	public void setMovementListener (MovementListener movementListener) {
		this.movementListener = movementListener;
	}

	private Node selectedNode;
	private LatLon originalCoord;
	private LatLon latestCoord;
	private void select (Node node) {
		selectedNode = node;
		latestCoord = null;
		resetOriginalCoord(node);
	}
	private void update (Node node) {
		if (node!=null && node.equals(selectedNode)) {
			latestCoord = selectedNode.getCoor();
		}
	}
	private void resetOriginalCoord (Node node) {
		if (node!=null && node == selectedNode) {
			originalCoord = node.getCoor();
			latestCoord = null;
		}
	}
	private void resetOriginalCoord() {
		resetOriginalCoord(selectedNode);
	}

	@Override
	public void primitivesAdded(PrimitivesAddedEvent event) {
		// Nothing to do
	}

	@Override
	public void primitivesRemoved(PrimitivesRemovedEvent event) {
		// Nothing to do
	}

	@Override
	public void tagsChanged(TagsChangedEvent event) {
		// Nothing to do
	}

	double lastDistance = 0;
	@Override
	public void nodeMoved(NodeMovedEvent event) {
		if (isDragging) {
			update(event.getNode());
		} else {
			// Moved without mouse dragging (undo, redo and so on)
			resetOriginalCoord(event.getNode());
		}
	}

	@Override
	public void wayNodesChanged(WayNodesChangedEvent event) {
		// Nothing to do
	}

	@Override
	public void relationMembersChanged(RelationMembersChangedEvent event) {
		// Nothing to do
	}

	@Override
	public void otherDatasetChange(AbstractDatasetChangedEvent event) {
		// Nothing to do
	}

	@Override
	public void dataChanged(DataChangedEvent event) {
		// Nothing to do	
	}
	
	// Find first valid Node from OsmPrimitive set
	private Node findFirstNodeOfSelection (Set<OsmPrimitive> set) {
		Iterator<OsmPrimitive> ite = set.iterator();
		while (ite.hasNext()) {
			OsmPrimitive primitive = ite.next();
			// Node, Way or Relation
			Node node = null;
			if (primitive instanceof Node) {
				node = (Node) primitive;
			} else if (primitive instanceof Way) {
				node = ((Way) primitive).firstNode();
			} else if (primitive instanceof Relation) {
				node = findFirstNodeOfSelection(((Relation) primitive).getMemberPrimitives());
			}
			if (node != null) {
				return node;
			}
		}
		return null;
	}
	
	@Override
	public void selectionChanged(SelectionChangeEvent event) {
		Node node = findFirstNodeOfSelection(event.getSelection());
		if (node==null) {
			return;
		}
		// Select first "Node" and remember its original coordinate
		select(node);
	}

	@Override
	public void eventDispatched(AWTEvent event) {
		MouseEvent mouseEvent = (MouseEvent) event;
		if (mouseEvent.getID()==MouseEvent.MOUSE_RELEASED) {
			isDragging = false;
			LatLon from = originalCoord;
			LatLon to = latestCoord;
			resetOriginalCoord();
			if (from!=null && to!=null && movementListener!=null) {
				movementListener.onMove(from, to);
			}
		}
		else if (mouseEvent.getID()==MouseEvent.MOUSE_PRESSED) {
			isDragging = true;
			lastDistance = 0;
		}
	}
}