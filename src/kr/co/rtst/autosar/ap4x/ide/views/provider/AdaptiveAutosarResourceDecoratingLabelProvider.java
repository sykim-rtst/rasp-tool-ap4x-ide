package kr.co.rtst.autosar.ap4x.ide.views.provider;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;

public class AdaptiveAutosarResourceDecoratingLabelProvider extends DecoratingLabelProvider implements ILabelProvider {

	public AdaptiveAutosarResourceDecoratingLabelProvider() {
		super(new AdaptiveAutosarResourceLebelProvider(), new AdaptiveAutosarResourceDecorator());
	}

}
