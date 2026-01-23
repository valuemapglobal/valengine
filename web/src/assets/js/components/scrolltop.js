'use strict'

import KTUtil from './util'

// Component Definition
const KTScrolltop = function (elementId, options) {
  // Main object
  let the = this
  let init = false

  // Get element object
  const element = KTUtil.getById(elementId)
  const body = KTUtil.getBody()

  if (!element) {
    return
  }

  // Default options
  let defaultOptions = {
    offset: 300,
    speed: 6000
  }

  /// /////////////////////////
  // ** Private Methods  ** //
  /// /////////////////////////

  var Plugin = {
    /**
         * Run plugin
         * @returns {mscrolltop}
         */
    construct: function (options) {
      if (KTUtil.data(element).has('scrolltop')) {
        the = KTUtil.data(element).get('scrolltop')
      } else {
        // reset scrolltop
        Plugin.init(options)

        // build scrolltop
        Plugin.build()

        KTUtil.data(element).set('scrolltop', the)
      }

      return the
    },

    /**
         * Handles subscrolltop click toggle
         * @returns {mscrolltop}
         */
    init: function (options) {
      the.events = []

      // merge default and user defined options
      the.options = KTUtil.deepExtend({}, defaultOptions, options)
    },

    build: function () {
      let timer

      window.addEventListener('scroll', function () {
        KTUtil.throttle(timer, function () {
          Plugin.handle()
        }, 200)
      })

      // handle button click
      KTUtil.addEvent(element, 'click', Plugin.scroll)
    },

    /**
         * Handles scrolltop click scrollTop
         */
    handle: function () {
      const pos = KTUtil.getScrollTop() // current vertical position

      if (pos > the.options.offset) {
        if (body.hasAttribute('data-scrolltop') === false) {
          body.setAttribute('data-scrolltop', 'on')
        }
      } else {
        if (body.hasAttribute('data-scrolltop') === true) {
          body.removeAttribute('data-scrolltop')
        }
      }
    },

    /**
         * Handles scrolltop click scrollTop
         */
    scroll: function (e) {
      e.preventDefault()

      KTUtil.scrollTop(0, the.options.speed)
    },

    /**
         * Trigger events
         */
    eventTrigger: function (name, args) {
      for (let i = 0; i < the.events.length; i++) {
        const event = the.events[i]
        if (event.name == name) {
          if (event.one == true) {
            if (event.fired == false) {
              the.events[i].fired = true
              return event.handler.call(this, the, args)
            }
          } else {
            return event.handler.call(this, the, args)
          }
        }
      }
    },

    addEvent: function (name, handler, one) {
      the.events.push({
        name: name,
        handler: handler,
        one: one,
        fired: false
      })
    }
  }

  /// ///////////////////////
  // ** Public Methods ** //
  /// ///////////////////////

  /**
     * Set default options
     */

  the.setDefaults = function (options) {
    defaultOptions = options
  }

  /**
     * Get subscrolltop mode
     */
  the.on = function (name, handler) {
    return Plugin.addEvent(name, handler)
  }

  /**
     * Set scrolltop content
     * @returns {mscrolltop}
     */
  the.one = function (name, handler) {
    return Plugin.addEvent(name, handler, true)
  }

  /// ////////////////////////////
  // ** Plugin Construction ** //
  /// ////////////////////////////

  // Run plugin
  Plugin.construct.apply(the, [options])

  // Init done
  init = true

  // Return plugin instance
  return the
}

// webpack support
if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
  module.exports = KTScrolltop
}

export default KTScrolltop
