package cn.edu.nju.raua.comment.parser;

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
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * 从源码中提取相关信息的基类
 */
public abstract class BaseInfomationExtractor {
	
	/**
	 * 表示从中提取信息的Java项目。
	 */
	protected IJavaProject project;
	
	/**
	 * 构造函数
	 * @param projectName 需要提取信息的项目名称。
	 */
	public BaseInfomationExtractor(String projectName) {
		IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
		IJavaModel javaModel = JavaCore.create(workspace);
		project = javaModel.getJavaProject(projectName);
	}
	
	/**
	 * 从指定项目的AST中提取所需信息。
	 * @return 返回提取的信息
	 */
	public Object extract() {
		IPackageFragmentRoot[] roots;
		try {
			roots = project.getAllPackageFragmentRoots();
			for (int i = 0; i < roots.length; i++)
			{
				IPackageFragmentRoot root = roots[i];
				switch (root.getKind())
				{
					case IPackageFragmentRoot.K_SOURCE:
						IJavaElement[] elem = root.getChildren();
						for (int j = 0; j < elem.length; j++)
						{
							IJavaElement currentElement = elem[j];
							if (currentElement.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
								process((IPackageFragment)currentElement);
							}
						}
						break;
					case IPackageFragmentRoot.K_BINARY:
						break;
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		return getResult();
	}
	
	protected final void process(IPackageFragment fragment) throws JavaModelException
	{
		try
		{
			ICompilationUnit[] units = fragment.getCompilationUnits();

			for (ICompilationUnit compilationUnit : units)
			{
				ASTParser parser = ASTParser.newParser(AST.JLS8);
				parser.setProject(this.project);
				parser.setResolveBindings(true);
				parser.setBindingsRecovery(true);
				parser.setSource(compilationUnit);
				try
				{
					ASTNode node = parser.createAST(null);
					if (node instanceof CompilationUnit) {
						BaseCompilationUnitParser unitParser = getCompilationUnitParser(compilationUnit, (CompilationUnit)node);
						node.accept(unitParser);
						appendResult(unitParser);
					}
				}
				catch (RuntimeException thr)
				{
					throw thr;
				}
			}
		}
		catch (RuntimeException thr)
		{
			thr.printStackTrace();
		}
	}
	
	/**
	 * 返回用于从CompilationUnit中提取信息的{@link cn.edu.nju.raua.comment.parser.BaseCompilationUnitParser}。
	 * @param unit 对应的Java compilation unit(source file with one of the Java-like extensions).
	 * @param astNode {@link org.eclipse.jdt.core.dom.CompilationUnit}类型的实例
	 * @return 一个{@link cn.edu.nju.raua.comment.parser.BaseCompilationUnitParser}实例
	 */
	protected abstract BaseCompilationUnitParser getCompilationUnitParser(ICompilationUnit unit, CompilationUnit astNode);
	
	/**
	 * 将从某个{@link org.eclipse.jdt.core.dom.CompilationUnit}中提取的信息保存到最终的结果。
	 * @param unitParser {@link cn.edu.nju.raua.comment.parser.BaseCompilationUnitParser}的一个实例，保存了
	 * 从某个 {@link org.eclipse.jdt.core.dom.CompilationUnit}中提取的信息
	 */
	protected abstract void appendResult(BaseCompilationUnitParser unitParser);
	
	/**
	 * 返回最终的提取信息。
	 * @return 返回最终的结果
	 */
	protected abstract Object getResult();
}
