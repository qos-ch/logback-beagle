package ch.qos.logback.beagle.view;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;

import ch.qos.logback.beagle.Constants;

public class SashListener implements Listener {

  int limit = 20;
  final Sash sash;
  final FormData formData;

  SashListener(Sash sash, FormData formData) {
    this.sash = sash;
    this.formData = formData;

  }

  @Override
  public void handleEvent(Event e) {
    Rectangle sashRect = sash.getBounds();
    Composite parent = sash.getParent();
    Rectangle parentRect = parent.getClientArea();

    int rightLimit = parentRect.width - sashRect.width - limit;
    if(e.x  > rightLimit)
      return;
    
    //int rightTarget = Math.min(e.x, rightLimit);
    
    int targetX = Math.max(e.x, limit);
     
    if (e.x != sashRect.x) {
      sash.getLayoutData();
      formData.left = new FormAttachment(0, targetX);
      formData.right = new FormAttachment(0, targetX+Constants.SASH_WIDTH);
      
      parent.layout();
    }

  }
}
