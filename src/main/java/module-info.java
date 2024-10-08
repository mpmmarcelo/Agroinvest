module com.shineworks.agroinvest {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires mongo.java.driver;
    requires java.desktop;
    requires poi.ooxml;

    opens com.shineworks.agroinvest to javafx.fxml;
    exports com.shineworks.agroinvest;
}