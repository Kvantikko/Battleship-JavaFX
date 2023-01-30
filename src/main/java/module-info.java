// declarate module imports and exports here
// see: https://tech.utugit.fi/soft/tools/lectures/tko8971/build/jpms/

module fi.utu.tech.gui.javafx {
    // requires java.desktop;
    requires transitive javafx.base;
    requires transitive javafx.fxml;
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires transitive javafx.media;
    requires transitive javafx.web;
    requires java.desktop;

    exports fi.utu.tech.gui.javafx;

    opens fi.utu.tech.gui.javafx;
}
