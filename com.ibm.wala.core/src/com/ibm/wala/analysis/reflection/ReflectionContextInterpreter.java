/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wala.analysis.reflection;

import java.util.Iterator;

import com.ibm.wala.cfg.ControlFlowGraph;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.ReflectionSpecification;
import com.ibm.wala.ipa.callgraph.propagation.SSAContextInterpreter;
import com.ibm.wala.ipa.callgraph.propagation.cfa.DelegatingSSAContextInterpreter;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.DefUse;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.types.FieldReference;

/**
 * {@link SSAContextInterpreter} to handle all reflection procession.
 * 
 * @author sjfink
 * 
 */
public class ReflectionContextInterpreter {

  public static SSAContextInterpreter createReflectionContextInterpreter(IClassHierarchy cha, AnalysisOptions options,
      AnalysisCache cache, ReflectionSpecification userSpec) {
    // start with a dummy interpreter that understands nothing
    SSAContextInterpreter result = new SSAContextInterpreter() {

      public boolean understands(CGNode node) {
        return false;
      }

      public boolean recordFactoryType(CGNode node, IClass klass) {
        // TODO Auto-generated method stub
        return false;
      }

      public Iterator<NewSiteReference> iterateNewSites(CGNode node) {
        // TODO Auto-generated method stub
        return null;
      }

      public Iterator<FieldReference> iterateFieldsWritten(CGNode node) {
        // TODO Auto-generated method stub
        return null;
      }

      public Iterator<FieldReference> iterateFieldsRead(CGNode node) {
        // TODO Auto-generated method stub
        return null;
      }

      public Iterator<CallSiteReference> iterateCallSites(CGNode node) {
        // TODO Auto-generated method stub
        return null;
      }

      public int getNumberOfStatements(CGNode node) {
        // TODO Auto-generated method stub
        return 0;
      }

      public IR getIR(CGNode node) {
        // TODO Auto-generated method stub
        return null;
      }

      public DefUse getDU(CGNode node) {
        // TODO Auto-generated method stub
        return null;
      }

      public ControlFlowGraph<ISSABasicBlock> getCFG(CGNode n) {
        // TODO Auto-generated method stub
        return null;
      }
    };

    if (!options.getReflectionOptions().isIgnoreFlowToCasts()) {
      // need the factory bypass interpreter
      result = new DelegatingSSAContextInterpreter(new FactoryBypassInterpreter(options, cache, userSpec), result);
    }
    if (!options.getReflectionOptions().isIgnoreStringConstants()) {
      result = new DelegatingSSAContextInterpreter(new DelegatingSSAContextInterpreter(new GetClassContextInterpeter(),
          new JavaLangClassContextInterpreter()), new DelegatingSSAContextInterpreter(new DelegatingSSAContextInterpreter(
          new ClassFactoryContextInterpreter(), new ClassNewInstanceContextInterpreter(cha)), result));
    }
    if (!options.getReflectionOptions().isIgnoreMethodInvoke()) {
      result = new DelegatingSSAContextInterpreter(new ReflectiveInvocationInterpreter(), result);
    }
    return result;
  }

}
