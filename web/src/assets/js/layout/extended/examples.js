'use strict'

const KTLayoutExamples = (function () {
  const initDefaultMode = function (element) {
    let elements = element
    if (typeof elements === 'undefined') {
      elements = document.querySelectorAll('.example:not(.example-compact):not(.example-hover):not(.example-basic)')
    }

    if (elements && elements.length > 0) {
      for (let i = 0; i < elements.length; ++i) {
        const example = elements[i]
        const copy = KTUtil.find(example, '.example-copy')

        if (copy) {
          const clipboard = new ClipboardJS(copy, {
            target: function (trigger) {
              const example = trigger.closest('.example')
              let el = KTUtil.find(example, '.example-code .tab-pane.active')

              if (!el) {
                el = KTUtil.find(example, '.example-code')
              }

              return el
            }
          })

          clipboard.on('success', function (e) {
            KTUtil.addClass(e.trigger, 'example-copied')
            e.clearSelection()

            setTimeout(function () {
              KTUtil.removeClass(e.trigger, 'example-copied')
            }, 2000)
          })
        }
      }
    }
  }

  const initCompactMode = function (element) {
    var example, code, toggle, copy, clipboard

    var elements = element
    if (typeof elements === 'undefined') {
      var elements = document.querySelectorAll('.example.example-compact')
    }

    if (elements && elements.length > 0) {
      for (let i = 0; i < elements.length; ++i) {
        var example = elements[i]
        var toggle = KTUtil.find(example, '.example-toggle')
        var copy = KTUtil.find(example, '.example-copy')

        // Handle toggle
        KTUtil.addEvent(toggle, 'click', function () {
          const example = this.closest('.example')
          const code = KTUtil.find(example, '.example-code')
          const the = this

          if (KTUtil.hasClass(this, 'example-toggled')) {
            KTUtil.slideUp(code, 300, function () {
              KTUtil.removeClass(the, 'example-toggled')
              KTUtil.removeClass(code, 'example-code-on')
              KTUtil.hide(code)
            })
          } else {
            KTUtil.addClass(code, 'example-code-on')
            KTUtil.addClass(this, 'example-toggled')
            KTUtil.slideDown(code, 300, function () {
              KTUtil.show(code)
            })
          }
        })

        // Handle copy
        if (copy) {
          var clipboard = new ClipboardJS(copy, {
            target: function (trigger) {
              const example = trigger.closest('.example')
              let el = KTUtil.find(example, '.example-code .tab-pane.active')

              if (!el) {
                el = KTUtil.find(example, '.example-code')
              }

              return el
            }
          })

          clipboard.on('success', function (e) {
            KTUtil.addClass(e.trigger, 'example-copied')
            e.clearSelection()

            setTimeout(function () {
              KTUtil.removeClass(e.trigger, 'example-copied')
            }, 2000)
          })
        }
      }
    }
  }

  return {
    init: function (element, options) {
      initDefaultMode(element)
      initCompactMode(element)
    }
  }
}())

export default KTLayoutExamples
