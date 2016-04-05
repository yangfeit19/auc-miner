package cn.edu.nju.aucminer.client.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import cn.edu.nju.aucminer.recommender.InvocationsReplacement;

public class ReplacementTableLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		InvocationsReplacement replacement = (InvocationsReplacement)element;
		switch (columnIndex) {
			case 0:
				return replacement.getOldInvocations().get(0).getMethodInfo().getFullQualifiedName();
			case 1:
				return replacement.getNewInvocations().get(0).getMethodInfo().getFullQualifiedName();
			default: 
				break;
		}
		return null;
	}

}
