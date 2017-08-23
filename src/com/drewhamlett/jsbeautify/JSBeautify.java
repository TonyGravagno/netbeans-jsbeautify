package com.drewhamlett.jsbeautify;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.script.Invocable;
import javax.script.ScriptEngineManager;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.apache.commons.io.IOUtils;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Build",
        id = "com.drewhamlett.jsbeautify.JSBeautify")
@ActionRegistration(displayName = "#CTL_JSBeautify")
@ActionReferences({
    @ActionReference(path = "Menu/Source", position = 200)
    ,
	@ActionReference(path = "Loaders/text/javascript/Actions", position = 0)
    ,
	@ActionReference(path = "Editors/text/javascript/Popup", position = 400, separatorAfter = 450)
    ,
	@ActionReference(path = "Editors/text/x-json/Popup", position = 400, separatorAfter = 450)
})
@Messages("CTL_JSBeautify=JSBeautify")
public final class JSBeautify implements ActionListener {

    private final DataObject dataObject;
    private final String beautifyPath = "\\src\\com\\drewhamlett\\jsbeautify\\resources\\EinarLielmanisBeautify.js";
    private final String beautifyURL = "https://cdnjs.cloudflare.com/ajax/libs/js-beautify/1.6.14/beautify.js";
    private final Boolean useLocal = true; // change to false to get from CDN
    private final String NO_JAVASCRIPT_MESSAGE = "No JavaScript Engine available - Nashorn required. JSBeautifier will not work without JavaScript.";

    public JSBeautify(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {

        try {

            FileObject file = dataObject.getPrimaryFile();
            format(file);

        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void log(String text) {
        // added here for debugging, modify as desired
        System.out.println(">\n>\n>\n> " + text + "\n>\n>\n>\n");
    }

    public void format(FileObject file) throws BadLocationException {
        try {
            String script;
            if (useLocal) {
                Stream<String> quickscript = Files.lines(Paths.get(System.getProperty("user.dir") + beautifyPath));
                script = quickscript.collect(Collectors.joining("\n"));
            } else {
                script = IOUtils.toString(new URL(beautifyURL), "UTF-8");
            }

            EditorCookie ec = dataObject.getCookie(EditorCookie.class);

            final StyledDocument doc = ec.openDocument();
            final Reformat reformat = Reformat.get(doc);
            final String unformattedText = doc.getText(0, doc.getLength());
            final JEditorPane[] openedPanes = ec.getOpenedPanes();

            reformat.lock();

            NbDocument.runAtomic(doc, new Runnable() {
                @Override
                public void run() {

                    HashMap<String, Object> properties = new HashMap<>();
                    // alphabetized options to make it easier to ensure all of them are included

                    String braceStyle = JSBeautifyOptions.getInstance().getOption("braceStyle", "collapse");
                    properties.put("brace_style", braceStyle);

                    boolean breakChainedMethods = JSBeautifyOptions.getInstance().getOption("breakChainedMethods", false);
                    properties.put("break_chained_methods", breakChainedMethods);

                    boolean indentCase = JSBeautifyOptions.getInstance().getOption("indentCase", false);
                    properties.put("indent_case", indentCase);

                    int indentSize = JSBeautifyOptions.getInstance().getOption("indentSize", 1);
                    // settings below

                    int initialIndentLevel = JSBeautifyOptions.getInstance().getOption("initialIndentLevel", 0);
                    properties.put("indent_level", initialIndentLevel);

                    boolean jslintHappy = JSBeautifyOptions.getInstance().getOption("jslintHappy", false);
                    properties.put("jslint_happy", jslintHappy);

                    boolean keepArrayIndent = JSBeautifyOptions.getInstance().getOption("keepArrayIndent", false);
                    properties.put("keep_array_indentation", keepArrayIndent);

                    properties.put("max_preserve_newlines", false);

                    boolean preserveNewLines = JSBeautifyOptions.getInstance().getOption("preserveNewLines", true);
                    properties.put("preserve_newlines", preserveNewLines);

                    boolean spaceBeforeConditional = JSBeautifyOptions.getInstance().getOption("spaceBeforeConditional", true);
                    properties.put("space_before_conditional", spaceBeforeConditional);

                    boolean usePaddingSpaces = JSBeautifyOptions.getInstance().getOption("usePaddingSpaces", false);
                    properties.put("space_in_paren", usePaddingSpaces);


                    boolean useSpaceBeforeAnon = JSBeautifyOptions.getInstance().getOption("useSpaceBeforeAnon", false);
                    properties.put("space_after_anon_function", useSpaceBeforeAnon);


                    boolean useSpaceInEmptyParen = JSBeautifyOptions.getInstance().getOption("useSpaceInEmptyParen", false);
                    properties.put("space_in_empty_paren", useSpaceInEmptyParen);

                    boolean useTabs = JSBeautifyOptions.getInstance().getOption("useTabs", false);

                    int wrapLineLength = JSBeautifyOptions.getInstance().getOption("wrapLineLength", 0);
                    properties.put("wrap_line_length", wrapLineLength);

                    if (useTabs) {
                        properties.put("indent_char", "\t");
                        properties.put("indent_size", 1);
                    } else {
                        int size = 4;
                        if (indentSize == 0) {
                            size = 2;
                        } else if (indentSize == 1) {
                            size = 4;
                        } else {
                            size = 8;
                        }
                        properties.put("indent_size", size);
                    }

                    Object result = "";
                    try {
                        NashornScriptEngine engine
                                = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
                        if (engine == null) {
                            throw new Exception(NO_JAVASCRIPT_MESSAGE);
                        } else {
                            Invocable inv = (Invocable) engine;
                            engine.eval("global = this;\n" + script); // 'global' required to get access to function
                            try {
                                result = inv.invokeFunction("js_beautify", unformattedText, properties);
                            } catch (Exception e1) {
                                throw e1;
                            }
                        }
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        result = ""; // just in case
                    } finally {
                    }

                    if (!result.toString().equals("")) {
                        try {
                            String finalText = result.toString();
                            int pos = openedPanes[0].getCaretPosition();

                            doc.remove(0, doc.getLength());
                            doc.insertString(0, finalText, null);

                            try {
                                openedPanes[0].setCaretPosition(pos);
                            } catch (Exception e) {
                                openedPanes[0].setCaretPosition(doc.getLength());
                            }
                            ec.saveDocument(); // only do this if replacement was successful
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }
            );

            reformat.unlock();
        } catch (BadLocationException ex) { // deal with specific issues later only if required
            Exceptions.printStackTrace(ex);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }
}
