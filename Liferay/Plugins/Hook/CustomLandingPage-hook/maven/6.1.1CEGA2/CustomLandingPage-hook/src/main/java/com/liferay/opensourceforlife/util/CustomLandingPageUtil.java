/**
 * 
 */

package com.liferay.opensourceforlife.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * @author tejas.kanani
 */
public class CustomLandingPageUtil {

	public static String getLanguage(HttpServletRequest request)
		throws PortalException, SystemException {

		String language = StringPool.BLANK;
		Locale currentLocale = PortalUtil.getUser(request).getLocale();

		if (GetterUtil.getInteger(PropsUtil.get(PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE)) == 1 &&
			!currentLocale.equals(LocaleUtil.getDefault()) ||
			GetterUtil.getInteger(PropsUtil.get(PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE)) == 2) {

			language = buildI18NPath(currentLocale);
		}

		return language;
	}

	private static String buildI18NPath(Locale locale) {

		String languageId = LocaleUtil.toLanguageId(locale);

		if (Validator.isNull(languageId)) {
			return null;
		}

		if (LanguageUtil.isDuplicateLanguageCode(locale.getLanguage())) {
			Locale priorityLocale =
				LanguageUtil.getLocale(locale.getLanguage());

			if (locale.equals(priorityLocale)) {
				languageId = locale.getLanguage();
			}
		}
		else {
			languageId = locale.getLanguage();
		}

		return StringPool.SLASH.concat(languageId);
	}

	public static String getDisplayURL(
		HttpServletRequest request, boolean isPrivateLayout)
		throws PortalException, SystemException {

		String displayURL = StringPool.BLANK;

		Group group =
			GroupLocalServiceUtil.getUserGroup(
				PortalUtil.getCompanyId(request), PortalUtil.getUserId(request));

		int publicLayoutsPageCount = group.getPublicLayoutsPageCount();

		if (publicLayoutsPageCount > 0) {
			StringBundler sb = new StringBundler(5);

			sb.append(PortalUtil.getPathMain());
			sb.append("/my_sites/view?groupId=");
			sb.append(group.getGroupId());

			if (isPrivateLayout) {
				sb.append("&privateLayout=1");
			}
			else {
				sb.append("&privateLayout=0");
			}

			displayURL = sb.toString();
		}

		return displayURL;
	}
}
