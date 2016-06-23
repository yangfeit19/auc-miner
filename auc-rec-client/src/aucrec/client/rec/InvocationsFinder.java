package aucrec.client.rec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

public class InvocationsFinder {
	
	/**
	 * 需要进行 adaptation 的客户端应用程序
	 */
	private String clientProjectName;
	private IJavaProject clientProject;
	
	private IInvocationFilter apiUsageFilter;
	
	private List<InvocationInfo> apiUsageList;
	
	public InvocationsFinder(String clientProjectName, IInvocationFilter apiUsageFilter) {
		this.clientProjectName = clientProjectName;
		this.apiUsageFilter = apiUsageFilter;
		
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IJavaModel javaModel = JavaCore.create(workspaceRoot);
		this.clientProject = javaModel.getJavaProject(clientProjectName);
		
		this.apiUsageList = new ArrayList<InvocationInfo>();
	}
	
	public List<InvocationInfo> extractAPIUsage() {
		try {
			IPackageFragmentRoot[] roots = clientProject.getAllPackageFragmentRoots();
			for (int i = 0; i < roots.length; i++) {
				IPackageFragmentRoot root = roots[i];
				switch (root.getKind()) {
					case IPackageFragmentRoot.K_SOURCE:
						IJavaElement[] elements = root.getChildren();
						for (int j = 0; j < elements.length; j++) {
							IJavaElement currentElement = elements[j];
							if (currentElement.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
								IPackageFragment fragment = (IPackageFragment)currentElement;
								processPacekageFragment(fragment);
							}
						}
						break;
					default:
						break;
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return apiUsageList;
	}
	
	private void processPacekageFragment(IPackageFragment fragment) {
		try {
			ICompilationUnit[] units = fragment.getCompilationUnits();
			for (ICompilationUnit compilationUnit : units) {
				
				ASTParser parser = ASTParser.newParser(AST.JLS8);
				parser.setProject(this.clientProject);
				parser.setResolveBindings(true);
				parser.setBindingsRecovery(true);
				parser.setSource(compilationUnit);
				
				ASTNode node = parser.createAST(null);
				if (node instanceof CompilationUnit) {
					InvocationExtractor invocationExtractor = new InvocationExtractor(
							clientProjectName,
							compilationUnit, 
							(CompilationUnit)node, 
							this.apiUsageFilter);
					
					node.accept(invocationExtractor);
					this.apiUsageList.addAll(invocationExtractor.getAPIUsageList());
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	class InvocationExtractor extends ASTVisitor {
		
		private String projectName;
		private ICompilationUnit compilaionUnit;
		private CompilationUnit astNode;
		private IInvocationFilter apiUsageFiletr;
		
		private String fileFullPath;
		private List<InvocationInfo> apiUsageList;
		
		public InvocationExtractor(String projectName, ICompilationUnit compilationUnit, CompilationUnit astNode, IInvocationFilter apiUsageFilter) {
			this.projectName = projectName;
			this.compilaionUnit = compilationUnit;
			this.astNode = astNode;
			this.apiUsageFiletr = apiUsageFilter;
			
			this.fileFullPath = this.compilaionUnit.getResource().getLocation().toString();
			this.apiUsageList = new ArrayList<InvocationInfo>();
		}
		
		public List<InvocationInfo> getAPIUsageList() {
			return this.apiUsageList;
		}
		
		@Override
		public boolean visit(ClassInstanceCreation node) {
			addInvocationInfo(node, node.getType().toString(), node.resolveConstructorBinding());
			return super.visit(node);
		}
		
		@Override
		public boolean visit(SuperConstructorInvocation node) {
			if (node.resolveConstructorBinding() != null) {
				addInvocationInfo(node, node.resolveConstructorBinding().getName(), node.resolveConstructorBinding());
			}
			return super.visit(node);
		}
		
		@Override
		public boolean visit(SuperMethodInvocation node) {
			addInvocationInfo(node, node.getName().toString(), node.resolveMethodBinding());
			return super.visit(node);
		}
		
		@Override
		public boolean visit(MethodInvocation node) {
			addInvocationInfo(node, node.getName().toString(), node.resolveMethodBinding());
			return super.visit(node);
		}
		
		@Override
		public boolean visit(FieldAccess node) {
			addFieldAccess(node, node.getName().toString(), node.resolveFieldBinding());
			return super.visit(node);
		}
		
		@Override
		public boolean visit(QualifiedName node) {
			IBinding binding = node.resolveBinding();
			if ((binding != null) && (binding.getKind() == IBinding.VARIABLE)) {
				if (binding instanceof IVariableBinding) {
					addFieldAccess(node, node.getName().toString(), (IVariableBinding)binding);
				}
			}
			return super.visit(node);
		}
		
		@Override
		public boolean visit(SuperFieldAccess node) {
			addFieldAccess(node, node.getName().toString(), node.resolveFieldBinding());
			return super.visit(node);
		}
		
		private void addInvocationInfo(ASTNode node, String name, IMethodBinding methodBinding) {
			String fullQualifiedName = getFullNameOfMethod(name, methodBinding, false);
			if (fullQualifiedName != null && !fullQualifiedName.isEmpty()) {
				
				int startPos = node.getStartPosition();
				int endPos = node.getStartPosition() + node.getLength();
				int startLineNum = astNode.getLineNumber(startPos);
				
				/*
				 * 对于泛型类的方法的调用，我们需要忽略该泛型类的类型参数。如：
				 * 
				 * class A<T1, T2> {
				 *  	.......
				 * 		public String af() {
				 * 			return "";
				 * 		}
				 *  	.......
				 * }
				 * 
				 * class B {
				 * 
				 *  	public void f() {
				 *  		A<String,String> aInstance = new A<String,String>();
				 *  		String str = aInstance.af();
				 * 		}
				 * }
				 * 
				 * 这里在提取方法B.f()调用的方法时，我们产生对A<,>.af()方法的调用，而不是
				 * 对A<String,String>.af()方法的调用。
				 */
				fullQualifiedName = generateGenericFullNameString(fullQualifiedName, true);
				
					
				InvocationInfo invocationInfo = new InvocationInfo(
						new MethodSignature(fullQualifiedName), 
						projectName, fileFullPath, 
						startLineNum, 
						startPos, 
						endPos);
				if (apiUsageFiletr.isAPIUsageOfSpecifiedLibrary(invocationInfo)) {
					apiUsageList.add(invocationInfo);
				}
			}
		}
		
		private String getFullNameOfMethod(String name, IMethodBinding methodBinding, boolean isCaller) {
			if (methodBinding != null)
			{
				ITypeBinding[] typeBinding = methodBinding.getParameterTypes();
				if (typeBinding != null)
				{
					StringBuilder sb = new StringBuilder();
					sb.append(methodBinding.getReturnType().getQualifiedName());
					sb.append("#");
					sb.append(getDeclaringClass(methodBinding.getDeclaringClass(), name));
					sb.append(".");
					sb.append(name);
					sb.append("(");
					if (typeBinding.length > 0) {
						sb.append(typeBinding[0].getQualifiedName());
						for (int i = 1; i < typeBinding.length; i++) {
							sb.append(",");
							sb.append(typeBinding[i].getQualifiedName());
						}
					}
					sb.append(")");
					return sb.toString();
				}
			}
			return "";
		}
		
		private String getDeclaringClass(ITypeBinding binding, String anonymous) {
			String fullName = binding.getQualifiedName();
			if (fullName.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				sb.append(getDeclaringClass(binding.getDeclaringClass(), "@"));
				sb.append(".");
				sb.append(anonymous);
				fullName = sb.toString();
			}
			return fullName;
		}
		
		private String generateGenericFullNameString(String fullQualifiedName, boolean isMethod) {
			int sharpCharIndex = fullQualifiedName.indexOf('#');
			if (sharpCharIndex >= 0) {
				int startPos = fullQualifiedName.indexOf('<', sharpCharIndex);
				int endPos = isMethod ? fullQualifiedName.indexOf("(") : fullQualifiedName.lastIndexOf(">.");
				
				if ((startPos > 0) && (endPos > 0)) {
					int leftAngleBracketCount = 0; 
					int typeStart = startPos + 1;
					Set<String> replacementsSet = new HashSet<String>();
					for (int i = startPos; i <= endPos; ++i) {
						if (fullQualifiedName.charAt(i) == '<') {
							leftAngleBracketCount++;
						} else if (fullQualifiedName.charAt(i) == '>') {
							leftAngleBracketCount--;
						}
						
						if (leftAngleBracketCount == 0) {
							if (fullQualifiedName.charAt(i) == '>') {
								if (i > typeStart) {
									replacementsSet.add(fullQualifiedName.substring(typeStart, i));
								}
								
								while (i + 1 < fullQualifiedName.length() && fullQualifiedName.charAt(i + 1) != '<') {
									i++;
								}
								
								typeStart = i + 2;
							}
						}
						else if (leftAngleBracketCount == 1) {
							if (fullQualifiedName.charAt(i) == ',') {
								if (i > typeStart) {
									replacementsSet.add(fullQualifiedName.substring(typeStart, i));
								}
								typeStart = i + 1;
							}
						}
					}
					
					for (String replacement : replacementsSet) {
						int replacementStart = 0;
						while (replacementStart >= 0 && replacementStart < fullQualifiedName.length()) {
							replacementStart = fullQualifiedName.indexOf(replacement, replacementStart);
							
							if (replacementStart < 0) break;
							
							int replacementEnd = replacementStart + replacement.length();
							if ((replacementStart == 0 || checkChar(fullQualifiedName.charAt(replacementStart - 1))) && 
									(replacementEnd == fullQualifiedName.length() || checkChar(fullQualifiedName.charAt(replacementEnd)))) {
								fullQualifiedName = fullQualifiedName.substring(0, replacementStart) + fullQualifiedName.substring(replacementEnd);
							}
							else {
								replacementStart = replacementEnd;
							}
						}
					}
				}
			}
			return fullQualifiedName;
		}
		
		private boolean checkChar(char ch) {
			if (ch == '.' || ('a' < ch && ch < 'z') || ('A' < ch && ch < 'Z') || ('0' < ch && ch < '9')) {
				return false;
			}
			return true;
		}
		
		private void addFieldAccess(ASTNode node, String name, IVariableBinding fieldBinding) {
			if (fieldBinding != null) {
				/*
				 * The declaring class of a field is the class or interface of which it is a member. 
				 * Local variables have no declaring class. The field length of an array type has no 
				 * declaring class.
				 */
				if (fieldBinding.getDeclaringClass() == null)
					return;
				ITypeBinding fieldType = fieldBinding.getType();
				if (fieldType != null) {
					StringBuilder sb = new StringBuilder();
					sb.append(fieldType.getQualifiedName());
					sb.append("#");
					sb.append(getDeclaringClass(fieldBinding.getDeclaringClass(), name));
					sb.append(".");
					sb.append(name);
					String fullQualifiedName = sb.toString();
					
					/*
					 * 对于泛型类的字段的访问，我们需要忽略该泛型类的类型参数。如：
					 * 
					 * class A<T1, T2> {
					 *  	.......
					 * 		public String a;
					 *  	.......
					 * }
					 * 
					 * class B {
					 * 
					 *  	public void f() {
					 *  		A<String,String> aInstance = new A<String,String>();
					 *  		String str = aInstance.a;
					 * 		}
					 * }
					 * 
					 * 这里在提取方法B.f()访问的字段时，我们产生对A<,>.a字段的访问，而不是
					 * 对A<String,String>.a的访问。
					 */
					fullQualifiedName = generateGenericFullNameString(fullQualifiedName, false);
					
					if (!fullQualifiedName.isEmpty()) {
						
						int startPos = node.getStartPosition();
						int endPos = node.getStartPosition() + node.getLength();
						int startLineNum = astNode.getLineNumber(startPos);
						
						InvocationInfo invocationInfo = new InvocationInfo(
								new MethodSignature(fullQualifiedName), 
								projectName, 
								fileFullPath, 
								startLineNum, 
								startPos, 
								endPos);
						if (apiUsageFiletr.isAPIUsageOfSpecifiedLibrary(invocationInfo)) {
							apiUsageList.add(invocationInfo);
						}
					}
				}
			}
		}	
	}
	
}
