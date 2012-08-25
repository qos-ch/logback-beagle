package ch.qos.logback.beagle.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import ch.qos.logback.beagle.Constants;
import ch.qos.logback.beagle.view.BeagleView;
import ch.qos.logback.beagle.view.TableMediator;

/**
 * Toggle logger tree.
 * 
 * @author Christian Trutz
 */
public class ToggleTreeActionDelegate implements IViewActionDelegate {

	private BeagleView beagleView = null;
	private FormAttachment oldLeft = null;
	private FormAttachment oldRight = null;
	private boolean hidden = false;

	@Override
	public void init(IViewPart view) {
		if (view instanceof BeagleView) {
			beagleView = (BeagleView) view;
		}
	}

	@Override
	public void run(IAction action) {
		if (beagleView != null) {
			TableMediator tableMediator = beagleView.getTableMediator();
			if (tableMediator != null) {
				Sash sash = tableMediator.getSash();
				FormData formData = (FormData) sash.getLayoutData();
				if (hidden) {
					hidden = false;
					formData.left = oldLeft;
					formData.right = oldRight;
				} else {
					hidden = true;
					oldLeft = formData.left;
					oldRight = formData.right;
					formData.left = new FormAttachment(0, 0);
					formData.right = new FormAttachment(0,
							0 + Constants.SASH_WIDTH);
				}
				sash.getParent().layout();
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// nothing to do
	}

}
