package com.ls.drupalcon.model;

public interface Listener<T1, T2>
{
	void onSucceeded(T1 result);
	void onFailed(T2 result);
}
