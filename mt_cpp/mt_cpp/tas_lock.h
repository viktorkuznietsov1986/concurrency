#pragma once
#include "ilock.h"
//#include <boost\atomic\atomic.hpp>
#include <atomic>

namespace locks {

	class tas_lock :
		public ilock
	{
	public:
		tas_lock();
		~tas_lock();

		virtual void lock();
		virtual void unlock();

	private:
		volatile std::atomic_bool locked;
	};

}

