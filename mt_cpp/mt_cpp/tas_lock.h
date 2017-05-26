#pragma once
#include "ilock.h"
#include <boost\atomic\atomic.hpp>

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
		volatile boost::atomic_bool locked;
	};

}

