package gmail.alexejkrawez;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Replication {

    private String path;
    private String pathDirOut;
    private String name;

    Replication(File path) {
        printFilesTree(path);
    }

    public void printFilesTree(File directory) {

        if (directory.exists()) {
            path = directory.getAbsolutePath();
            name = directory.getName();

            if (name.equals("ADC")) { // первая папка
                File directoryOut = new File(pathDirOut = path.replace("\\in", "\\out")); // создаёт папку ADC
                directoryOut.mkdirs();

                File[] subDirectory = directory.listFiles();
                if (subDirectory != null) {
                    for (File subWay : subDirectory) {
                        getContent(subWay, pathDirOut, 1);
                    }
                }

            }

        } else {
            System.out.println("Directory is not found.");
        }
    }

    private void getContent(File directory, String pathDirOut, int indents) {
        for (int i = 0; i < indents; i++) {
            System.out.print("\t");
        }

        if (directory.isFile()) {
            path = directory.getAbsolutePath();
            changeXML(path, pathDirOut);

        } else {

            name = directory.getName();
            pathDirOut = pathDirOut + File.separator + name.replace(name, "ADC_" + name);
            File directoryOut = new File(pathDirOut);
            directoryOut.mkdirs();
            System.out.println(directoryOut.getName()); // печатает в консоль названия изменённых папок

            File[] subDirectory = directory.listFiles();
            if (subDirectory != null) {
                for (File subWay : subDirectory) {
                    getContent(subWay, pathDirOut, indents + 1);
                }
            }

        }

    }

    private void changeXML(String path, String pathDirOut) {
        try {
            final File xmlFile = new File(path);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = db.parse(xmlFile);
            document.normalize();

            Node pointOfSale = document.getElementsByTagName("PointOfSale").item(0);
            NamedNodeMap attributes = pointOfSale.getAttributes();

            for (int n = 0; n < attributes.getLength(); n++) {
                Node node = attributes.item(n);

                if (node.getNodeName().equals("ParentPointOfSale")) {
                    node.setTextContent("ADC_" + node.getTextContent());
                } else if (node.getNodeName().equals("PointOfSaleCode")) {
                    node.setTextContent("ADC_" + node.getTextContent());
                }
            }

            Node pointOfSaleDescription = document.getElementsByTagName("PointOfSaleDescription").item(0);
            attributes = pointOfSaleDescription.getAttributes();

            for (int n = 0; n < attributes.getLength(); n++) {
                Node node = attributes.item(n);

                if (node.getNodeName().equals("Description")) {
                    node.setTextContent("ADC_" + node.getTextContent());
                }
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            pathDirOut = pathDirOut + File.separator
                    + xmlFile.getName().replace("PointOfSaleManageSvRQ_", "PointOfSaleManageSvRQ_ADC_");
            File file = new File(pathDirOut);
            System.out.println(file.getName()); // печатает в консоль названия изменённых xml-файлов
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (org.xml.sax.SAXException | IOException | ParserConfigurationException
                | TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException te) {
            te.printStackTrace();
        }

    }

}
