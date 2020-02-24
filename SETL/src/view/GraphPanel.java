package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import etl_model.ETLABox2TBox;
import etl_model.ETLABoxGenOperation;
import etl_model.ETLExpressionHandler;
import etl_model.ETLExtractionDB;
import etl_model.ETLExtractionSPARQL;
import etl_model.ETLFactEntryGenerator;
import etl_model.ETLInstanceEntryGenerator;
import etl_model.ETLLevelEntryGenerator;
import etl_model.ETLLoadingOperation;
import etl_model.ETLMappingGenOperation;
import etl_model.ETLMatcher;
import etl_model.ETLMultipleTransform;
import etl_model.ETLPWeightGenerator;
import etl_model.ETLRDFWrapper;
import etl_model.ETLResourceRetreiver;
import etl_model.ETLSBagGenerator;
import etl_model.ETLTBoxBuilder;
import etl_model.ETLUpdateDimensionalConstruct;
import view.PanelETL.Arrow;
import view.PanelETL.Operation;

//public class GraphPanel extends JPanel implements MouseListener, MouseMotionListener {
//	PanelETL panelETL = new PanelETL();
//	Graphics2D g2d;
//
//	// Association map contains association, i.e., which operation can
//	// follow which one
//	HashMap<String, ArrayList<String>> associationMap;
//
//	// Inputs given to a operation is are saved in some category so that
//	// they can be suggested in other operations
//
//	HashMap<String, LinkedHashSet<String>> inputMap;
//
//	final static String DB_NAME = "DB Name:";
//	final static String DB_USER_NAME = "DB User Name:";
//	final static String DB_USER_PASSWORD = "DB User Passoword:";
//	final static String BASE_IRI = "Base IRI:";
//	final static String FILE_PATH = "File Location:";
//	final static String FILE_NAME = "File Name:";
//
//	final static String KEY_WORD = "Key Word:";
//	final static String DBPEDIA_DATA_FILE = "DBpedia Data File:";
//	final static String RESOURCE_FILE = "Resource File:";
//	final static String PROPERTY_WEIGHT_FILE = "Property Weight File:";
//	final static String SEMANTIC_BAG_FILE = "Semantic Bag File:";
//	final static String RDF_FILE = "RDF File:";
//	final static String MAPPING_GRAPH_FILE = "Mapping Graph File:";
//	final static String TBox_FILE = "TBox File:";
//	final static String LEVEL_FILE = "Level File:";
//	final static String FACT_FILE = "Fact File:";
//	final static String INSTANCE_FILE = "Instance File:";
//	final static String DB_FILE = "Db File:";
//	final static String SPARQL_FILE = "Sparql File:";
//	final static String EXPRESSION_FILE = "Expression File:";
//	final static String RDF_MAPPER = "RDF Mapper:";
//	
//	public static final String START = "Start";
//	public static final String INSTANCE_ENTRY_GENERATOR = "InstanceGenerator";
//	public static final String NonSemanticToTBoxDeriver = "NonSemanticToTBoxDeriver";
//	public static final String RDF_WRAPPER = "RDFWrapper";
//	public static final String UPDATE_DIMENSION_CONSTRUCT = "UpdateDimensionalConstruct";
//	public static final String TransformationOnLiteral = "TransformationOnLiteral";
//	public static final String FACT_ENTRY_GENERATOR = "FactEntryGenerator";
//	public static final String LEVEL_ENTRY_GENERATOR = "LevelEntryGenerator";
//	public static final String MULTIPLE_TRANFORM = "JoinTransform";
//	public static final String MAPPING_GEN = "Direct Mapping Generator";
//	public static final String ABOX2TBOX = "ABoxToTBoxDeriver";
//	public static final String LOADER = "Loader";
//	public static final String SemanticSourceExtractor = "SemanticSourceExtractor";
//	public static final String EXT_DB = "DBExtractor";
//	public static final String RESOURCE_RETRIEVER = "Resource Retriever";
//	public static final String PWEIGHT_GENERATOR = "PWeight Generator";
//	public static final String SBAG_GENERATOR = "SBag Generator";
//	public static final String MATCHER = "Matcher";
//
//	boolean isDragged = false;
//
//	Operation selectedOperation;
//	
//	ArrayList<Operation> allOperations;
//	ArrayList<Arrow> allArrows;
//	
//	public GraphPanel() {
//		setBorder(new LineBorder(new Color(0, 0, 0), 1));
//		this.addMouseListener(this);
//		this.addMouseMotionListener(this);
//		setLayout(new BorderLayout(0, 0));
//		allOperations = new ArrayList<>();
//		allArrows = new ArrayList<>();
//		associationMap = getAssociationMap();
//		inputMap = getInitializedInputMap();
//		setBackground(Color.WHITE);
//	}
//	
//	private HashMap<String, LinkedHashSet<String>> getInitializedInputMap() {
//
//		HashMap<String, LinkedHashSet<String>> inputMap = new HashMap<>();
//
//		LinkedHashSet<String> stringList = new LinkedHashSet<>();
//		inputMap.put(DB_NAME, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(DB_USER_NAME, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(DB_USER_PASSWORD, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(BASE_IRI, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(FILE_PATH, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(FILE_NAME, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(MAPPING_GRAPH_FILE, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(RDF_FILE, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(TBox_FILE, stringList);
//
//		// Params for for level entry
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(LEVEL_FILE, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(FACT_FILE, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(SPARQL_FILE, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(EXPRESSION_FILE, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(RDF_MAPPER, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(NonSemanticToTBoxDeriver, stringList);
//
//		// Params for for linking
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(KEY_WORD, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(DBPEDIA_DATA_FILE, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(RESOURCE_FILE, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(PROPERTY_WEIGHT_FILE, stringList);
//
//		stringList = new LinkedHashSet<>();
//		inputMap.put(SEMANTIC_BAG_FILE, stringList);
//
//		return inputMap;
//	}
//	
//	private HashMap<String, ArrayList<String>> getAssociationMap() {
//
//		// This method returns the map containing the association between
//		// the operations
//
//		HashMap<String, ArrayList<String>> associations = new HashMap<>();
//
//		// This array list contains the operation that can be done after the
//		// particular operation
//
//		// Association for Start
//		ArrayList<String> association = new ArrayList<>();
//		association.add(SemanticSourceExtractor);
//		association.add(EXT_DB);
//		association.add(MAPPING_GEN);
//		association.add(TransformationOnLiteral);
//		association.add(MULTIPLE_TRANFORM);
//		association.add(RDF_WRAPPER);
//		association.add(NonSemanticToTBoxDeriver);
//		association.add(ABOX2TBOX);
//		association.add(LEVEL_ENTRY_GENERATOR);
//		association.add(FACT_ENTRY_GENERATOR);
//		association.add(INSTANCE_ENTRY_GENERATOR);
//		association.add(UPDATE_DIMENSION_CONSTRUCT);
//		association.add(RESOURCE_RETRIEVER);
//		association.add(PWEIGHT_GENERATOR);
//		association.add(SBAG_GENERATOR);
//		association.add(MATCHER);
//		association.add(LOADER);
//		associations.put(START, association);
//
//		// Association for T_BOX_BUILDER
//		association = new ArrayList<>();
//		associations.put(NonSemanticToTBoxDeriver, association);
//
//		// Association for Abox2tbox
//		association = new ArrayList<>();
//		associations.put(ABOX2TBOX, association);
//
//		// Association for RDF_WRAPPER
//		association = new ArrayList<>();
//		association.add(SemanticSourceExtractor);
//		association.add(LEVEL_ENTRY_GENERATOR);
//		association.add(FACT_ENTRY_GENERATOR);
//		association.add(INSTANCE_ENTRY_GENERATOR);
//		association.add(UPDATE_DIMENSION_CONSTRUCT);
//		associations.put(RDF_WRAPPER, association);
//
//		// Association for ExtDB
//		association = new ArrayList<>();
//		association.add(LEVEL_ENTRY_GENERATOR);
//		association.add(FACT_ENTRY_GENERATOR);
//		association.add(INSTANCE_ENTRY_GENERATOR);
//		associations.put(EXT_DB, association);
//
//		// Association for ExtSPARQL
//		association = new ArrayList<>();
//		association.add(TransformationOnLiteral);
//		association.add(LEVEL_ENTRY_GENERATOR);
//		association.add(FACT_ENTRY_GENERATOR);
//		association.add(INSTANCE_ENTRY_GENERATOR);
//		association.add(UPDATE_DIMENSION_CONSTRUCT);
//		associations.put(SemanticSourceExtractor, association);
//
//		// Association for EXPRESSION_HANDLER
//		association = new ArrayList<>();
//		association.add(RDF_WRAPPER);
//		association.add(NonSemanticToTBoxDeriver);
//		association.add(LEVEL_ENTRY_GENERATOR);
//		association.add(FACT_ENTRY_GENERATOR);
//		association.add(UPDATE_DIMENSION_CONSTRUCT);
//		associations.put(TransformationOnLiteral, association);
//		
//		// Association for MULTIPLE TRANSFORM
//		association = new ArrayList<>();
//		association.add(RDF_WRAPPER);
//		association.add(NonSemanticToTBoxDeriver);
//		association.add(LEVEL_ENTRY_GENERATOR);
//		association.add(FACT_ENTRY_GENERATOR);
//		association.add(UPDATE_DIMENSION_CONSTRUCT);
//		associations.put(MULTIPLE_TRANFORM, association);
//
//		// Association for Mapping generation
//		association = new ArrayList<>();
//		association.add(RDF_WRAPPER);
//		association.add(NonSemanticToTBoxDeriver);
//		association.add(ABOX2TBOX);
//		associations.put(MAPPING_GEN, association);
//
//		// Association for LEVEL_ENTRY_GENERATOR
//		association = new ArrayList<>();
//		association.add(LOADER);
//		associations.put(LEVEL_ENTRY_GENERATOR, association);
//
//		// Association for FACT_ENTRY_GENERATOR
//		association = new ArrayList<>();
//		association.add(LOADER);
//		associations.put(FACT_ENTRY_GENERATOR, association);
//
//		// Association for INSTANCE_GENERATOR
//		association = new ArrayList<>();
//		association.add(LOADER);
//		associations.put(INSTANCE_ENTRY_GENERATOR, association);
//
//		// Association for Update Dimensional Construct
//		association = new ArrayList<>();
//		association.add(LOADER);
//		associations.put(UPDATE_DIMENSION_CONSTRUCT, association);
//
//		// Association for Resource Retriever
//		association = new ArrayList<>();
//		association.add(PWEIGHT_GENERATOR);
//		association.add(SBAG_GENERATOR);
//		association.add(MATCHER);
//		association.add(LOADER);
//		associations.put(RESOURCE_RETRIEVER, association);
//
//		// Association for PWeight Generator
//
//		association = new ArrayList<>();
//		association.add(SBAG_GENERATOR);
//		association.add(MATCHER);
//		association.add(LOADER);
//		associations.put(PWEIGHT_GENERATOR, association);
//
//		// Association for SBag Generator
//
//		association = new ArrayList<>();
//		association.add(MATCHER);
//		association.add(LOADER);
//		associations.put(SBAG_GENERATOR, association);
//
//		// Association for Matcher
//		association = new ArrayList<>();
//		association.add(LOADER);
//		associations.put(MATCHER, association);
//
//		// Association for Loading
//		association = new ArrayList<>();
//		associations.put(LOADER, association);
//
//		return associations;
//	}
//
//	public void mouseReleased(MouseEvent e) {
//		// System.out.println("Released");
//
//		if (SwingUtilities.isLeftMouseButton(e)) {
//			String selectedOpName = "";
//
//			if (isDragged && selectedOpName.equals("Association")) {
//				int x = e.getX();
//				int y = e.getY();
//
//				for (Operation operation : allOperations) {
//					Shape shape = operation.getOperationShape();
//					if (shape.contains(x, y) && selectedOperation != getSelectedOperation(e.getX(), e.getY())) {
//						ArrayList<String> associationList = associationMap
//								.get(selectedOperation.getOperationName().toString());
//
//						if (associationList != null && associationList.contains(operation.getOperationName())) {
//
//							selectedOperation.getNextOperations().add(operation);
//							
//							Arrow arrow = new Arrow(selectedOperation, operation);
//							allArrows.add(arrow);
//							repaint();
//							isDragged = false;
//
//							break;
//
//						} else {
//
//							// System.out.println("Association List = " + associationList);
//							// System.out.println(operation.getOperationName());
//							JOptionPane.showMessageDialog(null,
//									"Association from " + selectedOperation.getOperationName() + " to "
//											+ operation.getOperationName() + " is invalid!");
//							break;
//						}
//					}
//
//				}
//
//			}
//
//			selectedOperation = null;
//			isDragged = false;
//
//		}
//
//	}
//
//	@Override
//	public void mouseDragged(MouseEvent arg0) {
//		if (SwingUtilities.isLeftMouseButton(arg0)) {
//			isDragged = true;
//
//			if (selectedOperation != null) {
//				selectedOperation.setUpperLeftX(arg0.getX());
//				selectedOperation.setUpperLeftY(arg0.getY());
//
//				repaint();
//			}
//		}
//
//	}
//
//	@Override
//	public void mouseMoved(MouseEvent arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public void mouseClicked(MouseEvent arg0) {
//		if (arg0.getClickCount() == 2) {
//			if (SwingUtilities.isLeftMouseButton(arg0)) {
//				Operation operation = getSelectedOperation(arg0.getX(), arg0.getY());
//				if (!operation.getOperationName().equals("Start")) {
//					boolean isInputTaken = operation.getEtlOperation().getInput(this, inputMap);
//
//					if (isInputTaken) {
//						if (!operation.isInputStatus()) {
//							operation.setInputStatus(isInputTaken);
//							repaint();
//						}
//					}
//				}
//			} else {
//				if (SwingUtilities.isRightMouseButton(arg0)) {
//					boolean found = false;
//					int option = -1;
//					ArrayList<Integer> arrowIndexes = new ArrayList<>();
//					int operationIndex = -1;
//
//					for (Operation operation : allOperations) {
//						Shape shape = operation.getOperationShape();
//						if (shape.contains(arg0.getX(), arg0.getY())) {
//							found = true;
//							option = JOptionPane.showConfirmDialog(null, "Are you sure to delete this "
//									+ operation.getOperationName()
//									+ " operation?\nAll association to this operation also will be deleted.",
//									"Confirmation", JOptionPane.YES_NO_OPTION);
//							if (option == 0) {
//								for (Arrow arrow : allArrows) {
//									if (arrow.targetOperation == operation || arrow.sourceOperation == operation) {
//										arrowIndexes.add(allArrows.indexOf(arrow));
//									}
//								}
//								operationIndex = allOperations.indexOf(operation);
//								break;
//							}
//						}
//					}
//
//					if (found) {
//						if (option == 0) {
//							for (int index : arrowIndexes) {
//								allArrows.remove(index);
//							}
//							allOperations.remove(operationIndex);
//							repaint();
//							return;
//						}
//					} else {
//						int arrowIndex = -1;
//						for (Arrow arrow : allArrows) {
//							ArrayList<Shape> arrowSegments = arrow.getArrowSegments();
//							for (Shape shape : arrowSegments) {
//								if (shape.contains(arg0.getX(), arg0.getY())) {
//									found = true;
//									break;
//								}
//							}
//
//							arrowIndex = allArrows.indexOf(arrow);
//							if (found)
//								break;
//						}
//
//						if (found) {
//							int op = JOptionPane.showConfirmDialog(null,
//									"Are you sure to delete association between \n"
//											+ allArrows.get(arrowIndex).getSourceOperation().getOperationName()
//											+ " and "
//											+ allArrows.get(arrowIndex).getTargetOperation().getOperationName(),
//									"Confirmation", JOptionPane.YES_NO_OPTION);
//							if (op == 0) {
//								allArrows.remove(arrowIndex);
//								repaint();
//								return;
//							}
//						}
//					}
//				}
//			}
//		} else {
//			if (SwingUtilities.isLeftMouseButton(arg0)) {
//				for (Operation operation : allOperations) {
//					Shape shape = operation.getOperationShape();
//					if (shape.contains(arg0.getX(), arg0.getY())) {
//						break;
//					}
//				}
//
//				for (Arrow arrow : allArrows) {
//					boolean found = false;
//					ArrayList<Shape> arrowSegments = arrow.getArrowSegments();
//					for (Shape shape : arrowSegments) {
//						if (shape.contains(arg0.getX(), arg0.getY())) {
//							found = true;
//							break;
//						}
//					}
//
//					if (found)
//						break;
//
//				}
//			}
//
//		}
//	}
//	
//
//	@Override
//	public void mouseEntered(MouseEvent arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void mouseExited(MouseEvent arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void mousePressed(MouseEvent e) {
//		// TODO Auto-generated method stub
//		if (SwingUtilities.isLeftMouseButton(e)) {
//			selectedOperation = getSelectedOperation(e.getX(), e.getY());
//		}
//	}
//	
//	public void paintComponent(Graphics g) {
//
//		// System.out.println("Paint Called");
//		g2d = (Graphics2D) g;
//		this.removeAll();
//		super.paintComponent(g);
//		// this.updateUI();
//
//		// For first time initialization of panel
//		if (allOperations.size() <= 0) {
//
//			Operation startOperation = new Operation("Start", 50, 150);
//			startOperation.setInputStatus(true);
//			startOperation.setOperationShape(
//					drawStart(g2d, startOperation.getUpperLeftX(), startOperation.getUpperLeftY()));
//
//			allOperations.add(startOperation);
//		} else {
//
//			for (Operation operation : allOperations) {
//
//				String operationName = operation.getOperationName();
//
//				if (operationName.equals("Start")) {
//					Shape newShape = drawStart(g2d, operation.getUpperLeftX(), operation.getUpperLeftY());
//					operation.setOperationShape(newShape);
//
//				} else {
//
//					Shape oldShape = operation.getOperationShape();
//
//					Color rectColor;
//
//					if (operation.isInputStatus()) {
//						rectColor = Color.decode("#0000FF");
//					} else {
//						rectColor = Color.RED;
//					}
//
//					Shape newShape = drawRoundRectangle(g2d, operation.getUpperLeftX(), operation.getUpperLeftY(),
//							operationName, rectColor);
//					operation.setOperationShape(newShape);
//				}
//			}
//		}
//
//		for (Arrow arrow : allArrows) {
//
//			// draw the arrows
//
//			ArrayList<Point> drawPoints = getDrawPoints(arrow.getSourceOperation().getOperationShape(),
//					arrow.getTargetOperation().getOperationShape());
//
//			Point startPoint = drawPoints.get(0);
//			Point endPoint = drawPoints.get(1);
//
//			ArrayList<Shape> arrowSeg = drawArrow(g2d, startPoint.x, startPoint.y, endPoint.x, endPoint.y,
//					Color.BLACK);
//
//			arrow.setArrowSegments(arrowSeg);
//		}
//
//	}
//
//
//
//	private Shape drawStart(Graphics2D graphics2d, double d, double e) {
//
//		Ellipse2D startShape = new Ellipse2D.Double(d, e, 80, 80);
//
//		graphics2d.setStroke(new BasicStroke(4));
//		graphics2d.setColor(Color.BLACK);
//		graphics2d.draw(startShape);
//		Ellipse2D startShapeFill = new Ellipse2D.Double(d + 2, e + 2, 76, 76);
//		graphics2d.setColor(Color.GREEN);
//		graphics2d.fill(startShapeFill);
//		graphics2d.setColor(Color.BLACK);
//
//		Font font = new Font("Tahoma", Font.BOLD, 18);
//		graphics2d.setFont(font);
//
//		FontMetrics fontMetrics = graphics2d.getFontMetrics();
//
//		// Determine the X coordinate for the text
//		int xCor = (int) (startShape.getX() + (startShape.getWidth() - fontMetrics.stringWidth("Start")) / 2);
//		// Determine the Y coordinate for the text (note we add the ascent,
//		// as
//		// in java 2d 0 is top of the screen)
//		int yCor = (int) (startShape.getY() + ((startShape.getHeight() - fontMetrics.getHeight()) / 2)
//				+ fontMetrics.getAscent());
//		// Draw the String
//		graphics2d.drawString("Start", xCor, yCor);
//
//		return startShape;
//	}
//
//	private ArrayList<Shape> drawArrow(Graphics2D graphics2d, int startX, int startY, int endX, int endY,
//			Color color) {
//
//		// contains the segments of an arrow
//		ArrayList<Shape> arrowSegments = new ArrayList<>();
//
//		graphics2d.setStroke(new BasicStroke(3));
//		graphics2d.setColor(color);
//		// temp variable to store line segments
//		Line2D tempLine2d;
//
//		if (startX == endX && startY < endY) {
//			// Vertical Down
//
//			if (endY - startY > 10) {
//
//				tempLine2d = new Line2D.Double(startX, startY, endX, endY - 10);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(startX - 5, endY - 10);
//				arrowHead.addPoint(startX + 5, endY - 10);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			} else {
//
//				// lowDistance, just draw the arrow head
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(startX - 5, startY);
//				arrowHead.addPoint(startX + 5, startY);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			}
//
//		} else if (startX == endX && startY > endY) {
//			// vertical Up
//
//			if (startY - endY > 10) {
//
//				tempLine2d = new Line2D.Double(startX, startY, endX, endY + 10);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(startX - 5, endY + 10);
//				arrowHead.addPoint(startX + 5, endY + 10);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			} else {
//
//				// lowDistance, just draw the arrow head
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(startX - 5, startY);
//				arrowHead.addPoint(startX + 5, startY);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			}
//
//		} else if (startY == endY && startX < endX) {
//			// Horizontal right
//
//			if (endX - startX > 10) {
//
//				tempLine2d = new Line2D.Double(startX, startY, endX - 10, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(endX - 10, endY - 5);
//				arrowHead.addPoint(endX - 10, endY + 5);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			} else {
//
//				// lowDistance, just draw the arrow head
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(startX, startY - 5);
//				arrowHead.addPoint(startX, startY + 5);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			}
//
//		} else if (startY == endY && startX > endX) {
//			// Horizontal left
//
//			if (startX - endX > 10) {
//
//				tempLine2d = new Line2D.Double(startX, startY, endX + 10, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(startX - 5, startY);
//				arrowHead.addPoint(startX + 5, startY);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			} else {
//
//				// lowDistance, just draw the arrow head
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(startX, startY - 5);
//				arrowHead.addPoint(startX, startY + 5);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			}
//
//		} else if (startY < endY && startX > endX) {
//			// Lower Left
//
//			int distance = startX - endX;
//			if (distance >= 12) {
//
//				int movePos = (distance - 10) / 2;
//
//				tempLine2d = new Line2D.Double(startX, startY, startX - movePos, startY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX - movePos, startY, startX - movePos, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX - movePos, endY, endX + 10, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(endX + 10, endY - 5);
//				arrowHead.addPoint(endX + 10, endY + 5);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			} else {
//				// not enough distance
//
//				tempLine2d = new Line2D.Double(startX, startY, startX - 1, startY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX - 1, startY, startX - 1, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX - 1, endY, startX - 2, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(startX - 2, endY - 5);
//				arrowHead.addPoint(startX - 2, endY + 5);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			}
//
//		} else if (startY < endY && startX < endX) {
//			// Lower right
//
//			int distance = endX - startX;
//			if (distance >= 12) {
//
//				int movePos = (distance - 10) / 2;
//
//				tempLine2d = new Line2D.Double(startX, startY, startX + movePos, startY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX + movePos, startY, startX + movePos, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX + movePos, endY, endX - 10, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(endX - 10, endY - 5);
//				arrowHead.addPoint(endX - 10, endY + 5);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			} else {
//				// not enough distance
//
//				tempLine2d = new Line2D.Double(startX, startY, startX + 1, startY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX + 1, startY, startX + 1, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX + 1, endY, startX + 2, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(startX + 2, endY - 5);
//				arrowHead.addPoint(startX + 2, endY + 5);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			}
//
//		} else if (startY > endY && startX > endX) {
//			// Upper Left
//
//			int distance = startX - endX;
//			if (distance >= 12) {
//
//				int movePos = (distance - 10) / 2;
//
//				tempLine2d = new Line2D.Double(startX, startY, startX - movePos, startY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX - movePos, startY, startX - movePos, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX - movePos, endY, endX + 10, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(endX + 10, endY - 5);
//				arrowHead.addPoint(endX + 10, endY + 5);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			} else {
//				// not enough distance
//
//				tempLine2d = new Line2D.Double(startX, startY, startX - 1, startY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX - 1, startY, startX - 1, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX - 1, endY, startX - 2, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(startX - 2, endY - 5);
//				arrowHead.addPoint(startX - 2, endY + 5);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			}
//
//		} else if (startY > endY && startX < endX) {
//			// Upper right
//
//			int distance = endX - startX;
//			if (distance >= 12) {
//
//				int movePos = (distance - 10) / 2;
//
//				tempLine2d = new Line2D.Double(startX, startY, startX + movePos, startY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX + movePos, startY, startX + movePos, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX + movePos, endY, endX - 10, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(endX - 10, endY - 5);
//				arrowHead.addPoint(endX - 10, endY + 5);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			} else {
//				// not enough distance
//
//				tempLine2d = new Line2D.Double(startX, startY, startX + 1, startY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX + 1, startY, startX + 1, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				tempLine2d = new Line2D.Double(startX + 1, endY, startX + 2, endY);
//				graphics2d.draw(tempLine2d);
//				arrowSegments.add(tempLine2d);
//
//				Polygon arrowHead = new Polygon();
//				arrowHead.addPoint(startX + 2, endY - 5);
//				arrowHead.addPoint(startX + 2, endY + 5);
//				arrowHead.addPoint(endX, endY);
//				graphics2d.fill(arrowHead);
//				arrowSegments.add(arrowHead);
//
//			}
//
//		}
//		return arrowSegments;
//	}
//
//	private RoundRectangle2D drawRoundRectangle(Graphics2D graphics2d, double x, double y, String text,
//			Color color) {
//
//		graphics2d.setColor(color);
//		graphics2d.setStroke(new BasicStroke(3));
//
//		// graphics2d.setColor(Color.RED);
//
//		// Default Height = 100, default width = 200
//		RoundRectangle2D rectangle = new RoundRectangle2D.Double(x, y, 200, 50, 10, 10);
//		graphics2d.draw(rectangle);
//
//		Font font = new Font("Tahoma", Font.BOLD, 12);
//		graphics2d.setFont(font);
//
//		FontMetrics fontMetrics = graphics2d.getFontMetrics();
//
//		// Determine the X coordinate for the text
//		int xCor = (int) (rectangle.getX() + (rectangle.getWidth() - fontMetrics.stringWidth(text)) / 2);
//		// Determine the Y coordinate for the text (note we add the ascent,
//		// as
//		// in java 2d 0 is top of the screen)
//		int yCor = (int) (rectangle.getY() + ((rectangle.getHeight() - fontMetrics.getHeight()) / 2)
//				+ fontMetrics.getAscent());
//		// Draw the String
//		graphics2d.setColor(Color.BLACK);
//		graphics2d.drawString(text, xCor, yCor);
//
//		return rectangle;
//
//	}
//
//	@Override
//	public Dimension getPreferredSize() {
//		return new Dimension(3000, 3000);
//	}
//
//	public void refreshPanel() {
//		repaint();
//
//	}
//
//	public void clearPanel() {
//		int option = JOptionPane.showConfirmDialog(null, "Are you sure?\nThis will clear all the ETL Flow.",
//				"Confirmation", JOptionPane.YES_NO_OPTION);
//		if (option == 0) {
//
//			allOperations = new ArrayList<>();
//			allArrows = new ArrayList<>();
//			repaint();
//
//		}
//	}
//	
//	private Operation getSelectedOperation(int x, int y) {
//		Shape selectedShape = null;
//		for (Operation operation : allOperations) {
//			selectedShape = operation.getOperationShape();
//			if (selectedShape.contains(x, y))
//				break;
//		}
//
//		for (Operation operation : allOperations) {
//			Shape shape = operation.getOperationShape();
//
//			if (shape == selectedShape) {
//				return operation;
//			}
//		}
//
//		return null;
//	}
//	
//	private ArrayList<String> getMappingGraphFiles() {
//
//		ArrayList<String> mappingGraphFilesPath = new ArrayList<>();
//		for (Operation operation : allOperations) {
//
//			if (operation.getOperationName().equals("Mapping Generation")) {
//
//				ETLMappingGenOperation mapGenOperation = (ETLMappingGenOperation) operation.getEtlOperation();
//				mappingGraphFilesPath.add(mapGenOperation.getFileSavingPath());
//			}
//		}
//
//		return mappingGraphFilesPath;
//	}
//	
//	private ArrayList<String> getRDFFiles() {
//		ArrayList<String> rdfFilesPath = new ArrayList<>();
//		for (Operation operation : allOperations) {
//			if (operation.getOperationName().equals("ABox Generation")) {
//				ETLABoxGenOperation aBoxGenOperation = (ETLABoxGenOperation) operation.getEtlOperation();
//				rdfFilesPath.add(aBoxGenOperation.getFileSavingPath());
//			}
//		}
//
//		return rdfFilesPath;
//	}
//	
//	private boolean setOperation(Operation operation) {
//
//		String operationName = operation.getOperationName();
//		boolean isMatched = false;
//		switch (operationName) {
//
//		case SemanticSourceExtractor:
//			operation.setEtlOperation(new ETLExtractionSPARQL());
//			isMatched = true;
//			break;
//
//		case EXT_DB:
//			operation.setEtlOperation(new ETLExtractionDB());
//			isMatched = true;
//			break;
//
//		case TransformationOnLiteral:
//			operation.setEtlOperation(new ETLExpressionHandler());
//			isMatched = true;
//			break;
//
//		case RDF_WRAPPER:
//			operation.setEtlOperation(new ETLRDFWrapper());
//			isMatched = true;
//			break;
//
//		case NonSemanticToTBoxDeriver:
//			operation.setEtlOperation(new ETLTBoxBuilder());
//			isMatched = true;
//			break;
//
//		case ABOX2TBOX:
//			operation.setEtlOperation(new ETLABox2TBox());
//			isMatched = true;
//			break;
//			
//		case MULTIPLE_TRANFORM:
//			operation.setEtlOperation(new ETLMultipleTransform());
//			isMatched = true;
//			break;
//
//		case LEVEL_ENTRY_GENERATOR:
//			operation.setEtlOperation(new ETLLevelEntryGenerator());
//			isMatched = true;
//			break;
//
//		case INSTANCE_ENTRY_GENERATOR:
//			operation.setEtlOperation(new ETLInstanceEntryGenerator());
//			isMatched = true;
//			break;
//
//		case FACT_ENTRY_GENERATOR:
//			operation.setEtlOperation(new ETLFactEntryGenerator());
//			isMatched = true;
//			break;
//
//		case UPDATE_DIMENSION_CONSTRUCT:
//			operation.setEtlOperation(new ETLUpdateDimensionalConstruct());
//			isMatched = true;
//			break;
//
//		case MAPPING_GEN:
//			operation.setEtlOperation(new ETLMappingGenOperation());
//			isMatched = true;
//			break;
//
//		case LOADER:
//			operation.setEtlOperation(new ETLLoadingOperation());
//			isMatched = true;
//			break;
//
//		case RESOURCE_RETRIEVER:
//			operation.setEtlOperation(new ETLResourceRetreiver());
//			isMatched = true;
//			break;
//
//		case PWEIGHT_GENERATOR:
//			operation.setEtlOperation(new ETLPWeightGenerator());
//			isMatched = true;
//			break;
//
//		case SBAG_GENERATOR:
//			operation.setEtlOperation(new ETLSBagGenerator());
//			isMatched = true;
//			break;
//
//		case MATCHER:
//			operation.setEtlOperation(new ETLMatcher());
//			isMatched = true;
//			break;
//
//		}
//
//		if (isMatched)
//			return true;
//		else
//			return false;
//	}
//
//	
//	
//	class Graph extends JPanel implements MouseListener, MouseMotionListener {
//		
//
//		@Override
//		public void mousePressed(MouseEvent e) {
//			// System.out.println("Pressed");
//
//			if (SwingUtilities.isLeftMouseButton(e)) {
//				selectedOperation = getSelectedOperation(e.getX(), e.getY());
//			}
//		}
//
//		@Override
//		public void mouseReleased(MouseEvent e) {
//			// System.out.println("Released");
//
//			if (SwingUtilities.isLeftMouseButton(e)) {
//				String selectedOpName = "";
//
//				if (selectedButton != null) {
//
//					if (selectedButton.getIcon() != null) {
//						selectedOpName = "Association";
//					} else {
//						selectedOpName = selectedButton.getText().toString();
//					}
//
//				}
//
//				if (isDragged && selectedOpName.equals("Association")) {
//
//					int x = e.getX();
//					int y = e.getY();
//
//					for (Operation operation : allOperations) {
//
//						Shape shape = operation.getOperationShape();
//
//						if (shape.contains(x, y) && selectedOperation != getSelectedOperation(e.getX(), e.getY())) {
//
//							ArrayList<String> associationList = associationMap
//									.get(selectedOperation.getOperationName().toString());
//
//							if (associationList != null && associationList.contains(operation.getOperationName())) {
//
//								selectedOperation.getNextOperations().add(operation);
//								Arrow arrow = new Arrow(selectedOperation, operation);
//								allArrows.add(arrow);
//								repaint();
//								isDragged = false;
//
//								desectAllPaletteButton();
//
//								break;
//
//							} else {
//
//								// System.out.println("Association List = " + associationList);
//								// System.out.println(operation.getOperationName());
//								JOptionPane.showMessageDialog(null,
//										"Association from " + selectedOperation.getOperationName() + " to "
//												+ operation.getOperationName() + " is invalid!");
//								break;
//							}
//						}
//
//					}
//
//				}
//
//				selectedOperation = null;
//				isDragged = false;
//
//			}
//
//		}
//
//		@Override
//		public void mouseDragged(MouseEvent arg0) {
//
//			if (SwingUtilities.isLeftMouseButton(arg0)) {
//
//				isDragged = true;
//
//				if (selectedOperation != null && selectedButton == null) {
//					selectedOperation.setUpperLeftX(arg0.getX());
//					selectedOperation.setUpperLeftY(arg0.getY());
//
//					repaint();
//				}
//			}
//
//		}
//
//		@Override
//		public void mouseMoved(MouseEvent arg0) {
//
//		}
//
//		private Operation getOperation(Shape shape) {
//
//			for (Operation operation : allOperations) {
//
//				if (operation.getOperationShape() == shape) {
//					return operation;
//				}
//			}
//			return null;
//		}
//
//		private ArrayList<Point> getDrawPoints(Shape sourceShape, Shape targetShape) {
//
//			ArrayList<Point> points = new ArrayList<>();
//
//			Point pointSource = new Point(0, 0);
//			Point pointDestination = new Point(0, 0);
//
//			if (sourceShape instanceof Ellipse2D) {
//
//				Ellipse2D sourceEllipse = (Ellipse2D) sourceShape;
//
//				double sourceMinX = sourceEllipse.getMinX();
//				double sourceMinY = sourceEllipse.getMinY();
//				double sourceMaxX = sourceEllipse.getMaxX();
//				double sourceMaxY = sourceEllipse.getMaxY();
//
//				if (targetShape instanceof RoundRectangle2D) {
//
//					RoundRectangle2D targetRectange = (RoundRectangle2D) targetShape;
//
//					double targetMinX = targetRectange.getMinX();
//					double targetMinY = targetRectange.getMinY();
//					double targetMaxX = targetRectange.getMaxX();
//					double targetMaxY = targetRectange.getMaxY();
//
//					if (targetMinX > sourceMaxX) {
//						// perfect Right to the source
//
//						// target points
//						pointDestination.x = (int) targetRectange.getMinX();
//						pointDestination.y = (int) (targetMinY + (targetRectange.getHeight() / 2));
//
//						// System.out.println("Right of the source");
//						pointSource.x = (int) sourceEllipse.getMaxX();
//						pointSource.y = (int) (sourceEllipse.getMinY() + (sourceEllipse.getHeight() / 2));
//
//					} else if (targetMaxX < sourceMinX) {
//						// perfect left to the source
//
//						// target points
//						pointDestination.x = (int) targetRectange.getMaxX();
//						pointDestination.y = (int) (targetMinY + (targetRectange.getHeight() / 2));
//
//						// System.out.println("Left of the Source");
//						pointSource.x = (int) sourceEllipse.getMinX();
//						pointSource.y = (int) (sourceEllipse.getMinY() + (sourceEllipse.getHeight() / 2));
//
//					} else {
//
//						pointSource.x = (int) sourceMinX;
//						pointSource.y = (int) (sourceMinY + (sourceEllipse.getHeight() / 2));
//
//						pointDestination.x = (int) targetMinX;
//						pointDestination.y = (int) (targetMinY + (targetRectange.getHeight() / 2));
//					}
//				}
//
//			} else {
//
//				// Source is a rectangle
//
//				RoundRectangle2D sourceRectengle = (RoundRectangle2D) sourceShape;
//				RoundRectangle2D targetRectangle = (RoundRectangle2D) targetShape;
//
//				double sourceMinX = sourceRectengle.getMinX();
//				double sourceMinY = sourceRectengle.getMinY();
//				double sourceMaxX = sourceRectengle.getMaxX();
//				double sourceMaxY = sourceRectengle.getMaxY();
//
//				double targetMinX = targetRectangle.getMinX();
//				double targetMinY = targetRectangle.getMinY();
//				double targetMaxX = targetRectangle.getMaxX();
//				double targetMaxY = targetRectangle.getMaxY();
//
//				if (targetMinX >= sourceMaxX) {
//					// right
//
//					pointSource.x = (int) sourceMaxX;
//					pointSource.y = (int) (sourceMinY + (sourceRectengle.getHeight() / 2));
//
//					pointDestination.x = (int) targetMinX;
//					pointDestination.y = (int) (targetMinY + (targetRectangle.getHeight() / 2));
//
//				} else if (targetMaxX < sourceMinX) {
//					// left
//
//					pointSource.x = (int) sourceMinX;
//					pointSource.y = (int) (sourceMinY + (sourceRectengle.getHeight() / 2));
//
//					pointDestination.x = (int) targetMaxX;
//					pointDestination.y = (int) (targetMinY + (targetRectangle.getHeight() / 2));
//
//				} else {
//
//					if (targetMaxY <= sourceMinY || targetMaxY <= sourceMaxY) {
//						// top
//
//						pointSource.y = (int) sourceMinY;
//						pointDestination.y = (int) targetMaxY;
//
//						int commonX = getCommonX(sourceMinX, sourceMaxX, targetMinX, targetMaxX);
//
//						pointSource.x = commonX;
//						pointDestination.x = commonX;
//
//					} else {
//
//						// bottom
//						pointSource.y = (int) sourceMaxY;
//						pointDestination.y = (int) targetMinY;
//
//						int commonX = getCommonX(sourceMinX, sourceMaxX, targetMinX, targetMaxX);
//
//						pointSource.x = commonX;
//						pointDestination.x = commonX;
//
//					}
//				}
//			}
//			points.add(pointSource);
//			points.add(pointDestination);
//			return points;
//		}
//
//		private int getCommonX(double line1MinX, double line1MaxX, double line2MinX, double line2MaxX) {
//
//			if (line1MinX == line2MinX && line1MaxX == line2MaxX) {
//				return (int) (line1MinX + ((line1MaxX - line1MinX) / 2));
//			} else {
//
//				double line1Length = line1MaxX - line1MinX;
//				double line2Length = line2MaxX - line2MinX;
//
//				if (line1MinX < line2MinX) {
//					// line 1 start left
//
//					double commonX1 = line2MinX;
//					double commonX2;
//					if (line1Length <= line2Length) {
//
//						commonX2 = commonX1 + (line1Length - (line2MinX - line1MinX));
//
//					} else {
//
//						commonX2 = commonX1 + (line2Length - (line2MinX - line1MinX));
//					}
//
//					return (int) (line2MinX + Math.ceil((commonX2 - commonX1) / 2));
//
//				} else {
//					// line 2 start left
//
//					double commonX1 = line1MinX;
//					double commonX2;
//					if (line1Length <= line2Length) {
//
//						commonX2 = commonX1 + (line1Length - (line1MinX - line2MinX));
//
//					} else {
//
//						commonX2 = commonX1 + (line2Length - (line1MinX - line2MinX));
//					}
//
//					return (int) (line1MinX + Math.ceil((commonX2 - commonX1) / 2));
//				}
//
//			}
//
//		}
//
//		// return where to draw arrow
//		private Point getDrawPoint(Shape shape) {
//			Point tempPoint = new Point(-1, -1);
//			if (shape instanceof RoundRectangle2D) {
//				// System.out.println("Shape is a rectengle");
//				RoundRectangle2D tempRect = (RoundRectangle2D) shape;
//				tempPoint.x = (int) tempRect.getMinX();
//
//				tempPoint.y = (int) (tempRect.getMinY() + (tempRect.getHeight() / 2));
//
//			} else if (shape instanceof Ellipse2D) {
//				Ellipse2D tempEllipse = (Ellipse2D) shape;
//				tempPoint.x = (int) tempEllipse.getMaxX();
//				tempPoint.y = (int) (tempEllipse.getMinY() + (tempEllipse.getHeight() / 2));
//			}
//
//			return tempPoint;
//		}
//
//	}
//
//	
//}
