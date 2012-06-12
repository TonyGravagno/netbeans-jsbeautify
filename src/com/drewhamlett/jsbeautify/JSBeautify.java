/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.drewhamlett.jsbeautify;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.netbeans.api.queries.FileEncodingQuery;
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

@ActionID( category = "Build",
id = "com.drewhamlett.jsbeautify.JSBeautify" )
@ActionRegistration( displayName = "#CTL_JSBeautify" )
@ActionReferences( {
    @ActionReference( path = "Menu/Source", position = 200 ),
    @ActionReference( path = "Loaders/text/javascript/Actions", position = 0 ),
    @ActionReference( path = "Editors/text/javascript/Popup", position = 400, separatorAfter = 450 )
} )
@Messages( "CTL_JSBeautify=JSBeautify" )
public final class JSBeautify implements ActionListener {

    private final DataObject dataObject;
    private Context context = null;

    public JSBeautify( DataObject dataObject ) {
	this.dataObject = dataObject;
    }

    @Override
    public void actionPerformed( ActionEvent ev ) {

	try {
	    
	    FileObject file = dataObject.getPrimaryFile();
	    format( file );

	} catch ( BadLocationException ex ) {
	    Exceptions.printStackTrace( ex );
	}
    }

    public void format( FileObject file ) throws BadLocationException {

	try {

	    EditorCookie ec = dataObject.getCookie( EditorCookie.class );

	    final StyledDocument doc = ec.openDocument();
	    final Reformat reformat = Reformat.get( doc );
	    final String unformattedText = doc.getText( 0, doc.getLength() );
	    final JEditorPane[] openedPanes = ec.getOpenedPanes();

	    reformat.lock();

	    try {

		NbDocument.runAtomic( doc, new Runnable() {

		    @Override
		    public void run() {

			try {

			    context = Context.enter();
			    context.setLanguageVersion( Context.VERSION_1_6 );
			    ScriptableObject scope = context.initStandardObjects();

			    Reader reader = new BufferedReader(
				    new InputStreamReader( getClass().getClassLoader().getResourceAsStream( "com/drewhamlett/jsbeautify/resources/beautify.js" ),
				    Charset.forName( "UTF-8" ) ) );

			    context.evaluateReader( scope, reader, "Beautify", 1, null );
			    Function fct = ( Function ) scope.get( "js_beautify", scope );
			    Object result = fct.call( context, scope, scope, new Object[]{ unformattedText } );

			    String finalText = result.toString();
			    int pos = openedPanes[0].getCaretPosition();
			    
			    doc.remove( 0, doc.getLength() );
			    doc.insertString( 0, finalText, null );

			    try {
				openedPanes[0].setCaretPosition( pos );
			    } catch ( Exception e ) {
				openedPanes[0].setCaretPosition( doc.getLength() );
			    }

			} catch ( BadLocationException ex ) {
			    Exceptions.printStackTrace( ex );
			} catch ( IOException ex ) {
			    Exceptions.printStackTrace( ex );
			}
		    }
		} );

	    } finally {
		reformat.unlock();
	    }

	    ec.saveDocument();

	} catch ( IOException ex ) {
	    Exceptions.printStackTrace( ex );
	} finally {
	    
	}
    }
}