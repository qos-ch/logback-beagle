/**
 * Logback-beagle: The logback Console Plugin for Eclipse 
 * Copyright (C) 2006-2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package ch.qos.logback.beagle.util;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;


/**
 * This class borrows very heavily from Eclipse's own EditorUtility class.
 */
public class EditorUtil {

  private static final String JAVA_EDITOR_ID = "org.eclipse.jdt.internal.ui.javaeditor.JavaEditor"; //$NON-NLS-1$

  public static void openInEditor(StackTraceElement ste) {
    IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
	.getProjects();

    IType type;
    IProject project;
    for (int i = 0; i < projects.length; i++) {
      project = projects[i];
      if (project.isOpen()) {
	try {
	  IJavaProject javaProject = JavaCore.create(project);
	  String className = ste.getClassName();
	  if (className == null) {
	    return;
	  }
	  type = findType(javaProject, className);
	  if (type != null) {
	    String path = type.getPath().toString();
	    if (path.startsWith('/' + project.getName())) {
	      path = path.substring(project.getName().length() + 1);
	    }
	    IFile file = project.getFile(path);
	    if (file.exists()) {
	      openInEditor(file, ste.getLineNumber());
	    }
	  }
	} catch (JavaModelException e) {
	  e.printStackTrace();
	}
      }
    }
  }

  private static void openInEditor(IFile file, int lineNumber) {
    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
	.getActivePage();
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put(IMarker.LINE_NUMBER, new Integer(lineNumber));
    map.put(IDE.EDITOR_ID_ATTR, JAVA_EDITOR_ID);
    try {
      IMarker marker = file.createMarker(IMarker.TEXT);
      marker.setAttributes(map);
      IDE.openEditor(page, marker);
      marker.delete();
    } catch (PartInitException e) {
      e.printStackTrace();
    } catch (CoreException e) {
      e.printStackTrace();
    }
  }

  private static IType findType(IJavaProject jproject, String fullyQualifiedName)
      throws JavaModelException {
    IType type = jproject.findType(fullyQualifiedName);
    if (type != null)
      return type;
    IPackageFragmentRoot[] roots = jproject.getPackageFragmentRoots();
    for (int i = 0; i < roots.length; i++) {
      IPackageFragmentRoot root = roots[i];
      type = findType(root, fullyQualifiedName);
      if (type != null && type.exists())
	return type;
    }
    return null;
  }

  private static IType findType(IPackageFragmentRoot root,
      String fullyQualifiedName) throws JavaModelException {
    IJavaElement[] children = root.getChildren();
    for (int i = 0; i < children.length; i++) {
      IJavaElement element = children[i];
      if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
	IPackageFragment pack = (IPackageFragment) element;
	if (!fullyQualifiedName.startsWith(pack.getElementName()))
	  continue;
	IType type = findType(pack, fullyQualifiedName);
	if (type != null && type.exists())
	  return type;
      }
    }
    return null;
  }

  private static IType findType(IPackageFragment pack, String fullyQualifiedName)
      throws JavaModelException {
    ICompilationUnit[] cus = pack.getCompilationUnits();
    for (int i = 0; i < cus.length; i++) {
      ICompilationUnit unit = cus[i];
      IType type = findType(unit, fullyQualifiedName);
      if (type != null && type.exists())
	return type;
    }
    return null;
  }

  private static IType findType(ICompilationUnit cu, String fullyQualifiedName)
      throws JavaModelException {
    IType[] types = cu.getAllTypes();
    for (int i = 0; i < types.length; i++) {
      IType type = types[i];
      if (getFullyQualifiedName(type).equals(fullyQualifiedName))
	return type;
    }
    return null;
  }

  private static String getFullyQualifiedName(IType type) {
    try {
      if (type.isBinary() && !type.isAnonymous()) {
	IType declaringType = type.getDeclaringType();
	if (declaringType != null) {
	  return getFullyQualifiedName(declaringType) + '.'
	      + type.getElementName();
	}
      }
    } catch (JavaModelException e) {
      // ignore
    }
    return type.getFullyQualifiedName('.');
  }
}
