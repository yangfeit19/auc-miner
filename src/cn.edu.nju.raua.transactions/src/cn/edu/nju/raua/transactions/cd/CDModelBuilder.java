package cn.edu.nju.raua.transactions.cd;

import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration; 
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import cn.edu.nju.raua.comment.parser.BaseCompilationUnitParser;
import cn.edu.nju.raua.comment.parser.BaseInfomationExtractor;

public class CDModelBuilder extends BaseInfomationExtractor
{
	private CDModel cdModel;
	private String saveDir;
	private boolean includeField;
	private boolean storeToFile;

	public CDModelBuilder(String projectName, String saveDirectory, boolean includeField, boolean storeToFile)
	{
		super(projectName);
		this.cdModel = new CDModel();
		this.saveDir = saveDirectory;
		this.includeField = includeField;
		this.storeToFile = storeToFile;
	}

	@Override
	public CDModel extract()
	{
		return (CDModel)super.extract();
	}

	@Override
	protected BaseCompilationUnitParser getCompilationUnitParser(ICompilationUnit unit, CompilationUnit astNode) {
		return new CDCreator(unit, astNode, includeField);
	}

	@Override
	protected void appendResult(BaseCompilationUnitParser unitParser) {
		CDCreator cdCreator = (CDCreator)unitParser;
		cdModel.appendCDModel(cdCreator.getResult());
	}

	@Override
	protected CDModel getResult() {
		if (storeToFile) {
			String filePath = saveDir + project.getElementName().replaceAll("\\s+", "_") + ".xml";
			outputCDModel(cdModel, filePath);
		}
		return cdModel;
	}
	
	private void outputCDModel(CDModel model, String filePath) {
		try
		{
			JAXBContext jc = JAXBContext.newInstance(CDModel.class);
			Marshaller marshaller = jc.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
			System.gc();
			FileOutputStream stream = new FileOutputStream(filePath);
			marshaller.marshal(model, stream);
			System.gc();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public class CDCreator extends BaseCompilationUnitParser
	{
		private CDModel model;
		private String fileFullPath;
		private boolean includedField = false;
		
		//When encountering inner or anonymous class declaration, 
		//save current class name.
		private Stack<String> classStack;
		
		//When encountering anonymous classes, save current caller.
		private Stack<MethodInfo> currentCallerStack;
		
		private ClassInstanceCreation anonymousClassConstructor;
		
		private String currentClass;
		private MethodInfo currentCaller;

		public CDCreator(ICompilationUnit unit, CompilationUnit astNode, boolean includeField)
		{
			super(unit, astNode);
			this.includedField = includeField;
			this.fileFullPath = unit.getResource().getRawLocation().toString();
			this.model = new CDModel();
			this.classStack = new Stack<String>();
			this.currentCallerStack = new Stack<MethodInfo>();
		}
		
		@Override
		public CDModel getResult() {
			return model;
		}
		
		@Override
		public boolean visit(TypeDeclaration node)
		{
			this.pushCurrentClassToStack();
			ITypeBinding typeBinding = node.resolveBinding();
			if (typeBinding != null) {
				this.currentClass = typeBinding.getQualifiedName();
				if (currentClass.isEmpty()) {
					this.currentClass = node.resolveBinding().getDeclaringClass().getQualifiedName() + "." + node.getName().toString();
				}
			} else {
				int startLine = astNode.getLineNumber(node.getStartPosition());
				System.out.println("TypeDeclaration.resolveBinding() == null. \n{\n" + fileFullPath + " : at line " + startLine + "\n}");
			}
			return super.visit(node);
		}
		
		@Override
		public void endVisit(TypeDeclaration node)
		{
			this.popCurrentClassFromStack();
			super.endVisit(node);
		}
		
		@Override
		public boolean visit(ClassInstanceCreation node)
		{
			this.anonymousClassConstructor = node;
			addCallee(node, node.getType().toString(), node.resolveConstructorBinding());
			return super.visit(node);
		}
		
		@Override
		public boolean visit(AnonymousClassDeclaration node)
		{
			this.pushCurrentClassToStack();
			if (this.anonymousClassConstructor != null) { 
				IMethodBinding methodBinding = this.anonymousClassConstructor.resolveConstructorBinding();
				if (methodBinding != null)
				{
					this.currentClass = methodBinding.getDeclaringClass().getPackage().getName() + "." + this.anonymousClassConstructor.getType();
				}
			} else {
				int startLine = astNode.getLineNumber(node.getStartPosition());
				System.out.println("this.anonymousClassConstructor == null. \n{\n" + fileFullPath + " : at line " + startLine + "\n}");
			}
			return super.visit(node);
		}

		@Override
		public void endVisit(AnonymousClassDeclaration node)
		{
			this.popCurrentClassFromStack();
			super.endVisit(node);
		}

		@Override
		public boolean visit(MethodDeclaration node)
		{
			pushCurrentCallerToStack();
			if (node != null) {
				int startLine = astNode.getLineNumber(node.getStartPosition());
				int endLine = astNode.getLineNumber(node.getStartPosition() + node.getLength());
				String fullQualifiedName = getFullNameOfMethod(node.getName().toString(), node.resolveBinding(), true);
				model.addDeclarationString(fullQualifiedName);
				this.currentCaller = new MethodInfo(startLine, endLine, fullQualifiedName, fileFullPath);
				
				// 添加定义信息
				model.addMethodDeclaration(this.currentClass, fullQualifiedName);
				
				return super.visit(node);
			}
			return false;
		}
		
		@Override
		public void endVisit(MethodDeclaration node) {
			popCurrentCallerFromStack();
			super.endVisit(node);
		}
		
		@Override
		public boolean visit(FieldDeclaration node) {
			if (node != null) {
				if (node.fragments().size() == 1) {
					VariableDeclarationFragment fragment = (VariableDeclarationFragment)node.fragments().get(0);
					IVariableBinding vBinding = fragment.resolveBinding();
					if (vBinding != null) {
						String fullQualifiedName = getFullNameOfField(fragment.getName().toString(), vBinding);
						model.addDeclarationString(fullQualifiedName);
						
						// 添加定义信息
						model.addMethodDeclaration(this.currentClass, fullQualifiedName);
					}
				}
			}
			return super.visit(node);
		}
		
		@Override
		public boolean visit(SuperConstructorInvocation node)
		{
			if (node.resolveConstructorBinding() != null)
			{
				if (this.addCallee(node, node.resolveConstructorBinding().getName(), node.resolveConstructorBinding()))
				{
					return super.visit(node);
				}
			}
			return false;
		}

		@Override
		public boolean visit(SuperMethodInvocation node)
		{
			if (this.addCallee(node, node.getName().toString(), node.resolveMethodBinding()))
			{
				return super.visit(node);
			}
			return false;
		}

		@Override
		public boolean visit(MethodInvocation node)
		{
			if (this.addCallee(node, node.getName().toString(), node.resolveMethodBinding()))
			{
				return super.visit(node);
			}
			return false;
		}
		
		@Override
		public boolean visit(FieldAccess node) {
			if (includedField) {
				if (node != null) {
					IVariableBinding fieldBinding = node.resolveFieldBinding();
					return addFieldAccess(node, node.getName().toString(), fieldBinding);
				}
			}
			return super.visit(node);
		}
		
		@Override
		public boolean visit(QualifiedName node) {
			if (includedField) {
				IBinding binding = node.resolveBinding();
				if ((binding != null) && (binding.getKind() == IBinding.VARIABLE)) {
					if (binding instanceof IVariableBinding) {
						addFieldAccess(node, node.getName().toString(), (IVariableBinding)binding);
					}
				}
			}
			return super.visit(node);
		}
		
		@Override
		public boolean visit(SuperFieldAccess node) {
			if (includedField) {
				if (node != null) {
					IVariableBinding fieldBinding = node.resolveFieldBinding();
					return addFieldAccess(node, node.getName().toString(), fieldBinding);
				}
			}
			return super.visit(node);
		}
		
		private String getFullNameOfField(String name, IVariableBinding fieldBinding) {
			ITypeBinding typeBinding = fieldBinding.getDeclaringClass();
			if ((fieldBinding != null) && (typeBinding != null)) {
				ITypeBinding fieldType = fieldBinding.getType();
				if (fieldType != null) {
					StringBuilder sb = new StringBuilder();
					sb.append(fieldType.getQualifiedName());
					sb.append("#");
					sb.append(getDeclaringClass(typeBinding, name));
					sb.append(".");
					sb.append(name);
					return sb.toString();
				}
			}
			return null;
		}
		
		private boolean addFieldAccess(ASTNode node, String name, IVariableBinding fieldBinding) {
			if (fieldBinding != null) {
				/*
				 * The declaring class of a field is the class or interface of which it is a member. 
				 * Local variables have no declaring class. The field length of an array type has no 
				 * declaring class.
				 */
				if (fieldBinding.getDeclaringClass() == null)
					return true;
				ITypeBinding fieldType = fieldBinding.getType();
				if (fieldType != null) {
					StringBuilder sb = new StringBuilder();
					sb.append(fieldType.getQualifiedName());
					sb.append("#");
					sb.append(getDeclaringClass(fieldBinding.getDeclaringClass(), name));
					sb.append(".");
					sb.append(name);
					
					if (currentCaller != null) {
						int startLine = astNode.getLineNumber(node.getStartPosition());
						int endLine = astNode.getLineNumber(node.getStartPosition() + node.getLength());
						String fullQualifiedName = sb.toString();
						MethodInfo callee = new MethodInfo(startLine, endLine, fullQualifiedName, fileFullPath);
						
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
						callee.fullQualifiedName = generateGenericFullNameString(callee.fullQualifiedName, false);
						
						if (!callee.fullQualifiedName.isEmpty()) {
							model.addCallRelationship(currentCaller, callee);
						}
					}
					return true;
				}
			}
			return false;
		}
		
		private boolean addCallee(ASTNode node, String name, IMethodBinding methodBinding)
		{
			String fullQualifiedName = getFullNameOfMethod(name, methodBinding, false);
			if ((fullQualifiedName != null) && (currentCaller != null))
			{
				int startLine = astNode.getLineNumber(node.getStartPosition());
				int endLine = astNode.getLineNumber(node.getStartPosition() + node.getLength());
				MethodInfo callee = new MethodInfo(startLine, endLine, fullQualifiedName, fileFullPath);
				
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
				callee.fullQualifiedName = generateGenericFullNameString(callee.fullQualifiedName, true);
				
				if (!callee.fullQualifiedName.isEmpty()) {
					model.addCallRelationship(currentCaller, callee);
				}
				
				return true;
			}
			return false;
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
		
		private String getFullNameOfMethod(String name, IMethodBinding methodBinding, boolean isCaller) {
			if (methodBinding != null)
			{
				ITypeBinding[] typeBinding = methodBinding.getParameterTypes();
				if (typeBinding != null)
				{
					StringBuilder sb = new StringBuilder();
					sb.append(methodBinding.getReturnType().getQualifiedName());
					sb.append("#");
					if (isCaller) {
						sb.append(this.currentClass);
					}
					else {
						sb.append(getDeclaringClass(methodBinding.getDeclaringClass(), name));
					}
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

		private void pushCurrentClassToStack()
		{
			if (this.currentClass != null)
			{
				this.classStack.push(this.currentClass);
			}
		}

		private void pushCurrentCallerToStack()
		{
			if (this.currentCaller != null)
			{
				this.currentCallerStack.push(this.currentCaller);
				this.currentCaller = null;
			}
		}

		private void popCurrentClassFromStack()
		{
			if (this.classStack.empty())
			{
				this.currentClass = null;
			}
			else
			{
				this.currentClass = this.classStack.pop();
			}
		}

		private void popCurrentCallerFromStack()
		{
			if (this.currentCallerStack.empty())
			{
				this.currentCaller = null;
			}
			else
			{
				this.currentCaller = this.currentCallerStack.pop();
			}
		}
	}
}
