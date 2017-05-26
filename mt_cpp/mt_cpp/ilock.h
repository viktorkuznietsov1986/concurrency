#pragma once
#ifndef _I_LOCK_H_
#define _I_LOCK_H_

namespace locks {

	class ilock
	{
	public:
		virtual void lock() = 0;
		virtual void unlock() = 0;
	};

}

#endif // _I_LOCK_H_
