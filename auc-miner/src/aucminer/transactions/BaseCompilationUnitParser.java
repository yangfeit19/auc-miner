package aucminer.transactions;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**从{@link org.eclipse.jdt.core.dom.CompilationUnit}类的实例中提取信息的抽象基类。
 * 该类扩展了{@link org.eclipse.jdt.core.dom.ASTVisitor}。
 */
public abstract class BaseCompilationUnitParser extends ASTVisitor {
	
	protected ICompilationUnit unit;
	protected CompilationUnit astNode;
	
	/**
	 * 构造函数
	 * @param unit 对应的Java compilation unit(source file with one of the Java-like extensions).
	 * @param astNode 需要从中提取信息的{@link org.eclipse.jdt.core.dom.CompilationUnit}类的实例
	 */
	public BaseCompilationUnitParser(ICompilationUnit unit, CompilationUnit astNode) {
		this.unit = unit;
		this.astNode = astNode;
	}
	
	/**
	 * 获取从org.eclipse.jdt.core.dom.CompilationUnit类的实例中提取的信息
	 * @return 解析得到的信息实现
	 */
	public abstract Object getResult();
}
