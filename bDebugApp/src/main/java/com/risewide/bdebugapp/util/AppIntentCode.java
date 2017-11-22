package com.risewide.bdebugapp.util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by birdea on 2017-10-12.
 */

public class AppIntentCode {

	/**
	 * <p>Application의 life cycle 인텐트</p>
	 * */
	public interface AppLifeCycle {
		String INTENT = "AppLifeCycle";
		/**
		 * Keys for {@link AppLifeCycle#INTENT}
		 */
		interface Key {
			/**
			 * The key of {@link LifeCycleValue}
			 */
			String LIFE_CYCLE = "LIFE_CYCLE";
		}
		/**
		 * Values of {@link Key#LIFE_CYCLE}
		 **/
		interface LifeCycleValue {
			String CREATE = "CREATE";
			String DESTROY = "DESTROY";
		}
	}

	/**
	 * <p>Activity의 life cycle 인텐트</p>
	 */
	public interface ActivityLifeCycle {
		String INTENT = "ActivityLifeCycle";
		/**
		 * Keys for {@link ActivityLifeCycle#INTENT}
		 */
		interface Key {
			/**
			 * A class name of activity
			 */
			String CLASS_NAME = "CLASS_NAME";
			/**
			 * The key of {@link LifeCycleValue}
			 */
			String LIFE_CYCLE = "LIFE_CYCLE";
		}
		/**
		 * Values of {@link Key#LIFE_CYCLE}
		 **/
		interface LifeCycleValue {
			String CREATE = "CREATE";
			String RESUME = "RESUME";
			String START = "START";
			String STOP = "STOP";
			String PAUSE = "PAUSE";
			String DESTROY = "DESTROY";
		}
	}

	/**
	 * <p>Application의 리스트 아이템 선택 행위에 필요한 인텐트</p>
	 * */
	public interface UiListItem {
		String INTENT_SELECT = "INTENT_SELECT";
		String INTENT_REBUILD = "INTENT_REBUILD";
		/**
		 * Keys for {@link UiListItem#INTENT_SELECT} {@link UiListItem#INTENT_REBUILD}
		 */
		interface Key {
			/** index = {0~N} or {2|1|3|0..} */
			String INDEX = "INDEX";
			/** value = {a} or {c|b|d|a|..}*/
			String VALUE = "VALUE";
			/** delimeter for split */
			String DELIMETER = "|";
		}

		class Helper {

			public static void addItem(StringBuilder bucket, String value){
				if (bucket == null) {
					return;
				}
				bucket.append(value).append(Key.DELIMETER);
			}

			public static List<String> getItems(String value) {
				return Arrays.asList(value.split("\\|"));
			}
		}
	}
}
