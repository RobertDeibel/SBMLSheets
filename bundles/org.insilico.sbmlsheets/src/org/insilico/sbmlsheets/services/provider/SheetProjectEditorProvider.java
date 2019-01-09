package org.insilico.sbmlsheets.services.provider;

import org.eclipse.fx.code.editor.fx.e4.EditorClassURLProvider;
import org.insilico.sbmlsheets.editor.SheetProjectView;
import org.osgi.service.component.annotations.Component;

@Component
public class SheetProjectEditorProvider implements EditorClassURLProvider {

    @Override
    public boolean test(String t) {
        return t.endsWith("sheets");
    }

    @Override
    public String getBundleClassURI(String uri) {
        return "bundleclass://org.insilico.sbmlsheets/" + SheetProjectView.class.getName();
    }
}
