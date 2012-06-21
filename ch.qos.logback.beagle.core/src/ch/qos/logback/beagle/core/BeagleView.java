package ch.qos.logback.beagle.core;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * @author Christian Trutz
 */
public class BeagleView extends ViewPart {

    //
    private TableViewer beagleTableViewer = null;

    @Override
    public void createPartControl(Composite parent) {
        Composite root = new Composite(parent, SWT.NONE);
        root.setLayout(new FillLayout());

        beagleTableViewer = new TableViewer(root);
        beagleTableViewer.setContentProvider(new ArrayContentProvider());
        beagleTableViewer.setLabelProvider(new LabelProvider());
        beagleTableViewer.setInput(new String[] { "hello" });
    }

    @Override
    public void setFocus() {
        if (beagleTableViewer != null)
            beagleTableViewer.getTable().setFocus();
    }

}
