'use strict'

const KTLayoutQuickCartPanel = (function () {
  // Private properties
  let _element
  let _offcanvasObject

  // Private functions
  const _init = function () {
    _offcanvasObject = new KTOffcanvas(_element, {
      overlay: true,
      baseClass: 'offcanvas',
      placement: 'right',
      closeBy: 'kt_quick_cart_close',
      toggleBy: 'kt_quick_cart_toggle'
    })

    const header = KTUtil.find(_element, '.offcanvas-header')
    const content = KTUtil.find(_element, '.offcanvas-content')
    const wrapper = KTUtil.find(_element, '.offcanvas-wrapper')
    const footer = KTUtil.find(_element, '.offcanvas-footer')

    KTUtil.scrollInit(wrapper, {
      disableForMobile: true,
      resetHeightOnDestroy: true,
      handleWindowResize: true,
      height: function () {
        let height = parseInt(KTUtil.getViewPort().height)

        if (header) {
          height = height - parseInt(KTUtil.actualHeight(header))
          height = height - parseInt(KTUtil.css(header, 'marginTop'))
          height = height - parseInt(KTUtil.css(header, 'marginBottom'))
        }

        if (content) {
          height = height - parseInt(KTUtil.css(content, 'marginTop'))
          height = height - parseInt(KTUtil.css(content, 'marginBottom'))
        }

        if (wrapper) {
          height = height - parseInt(KTUtil.css(wrapper, 'marginTop'))
          height = height - parseInt(KTUtil.css(wrapper, 'marginBottom'))
        }

        if (footer) {
          height = height - parseInt(KTUtil.actualHeight(footer))
          height = height - parseInt(KTUtil.css(footer, 'marginTop'))
          height = height - parseInt(KTUtil.css(footer, 'marginBottom'))
        }

        height = height - parseInt(KTUtil.css(_element, 'paddingTop'))
        height = height - parseInt(KTUtil.css(_element, 'paddingBottom'))

        height = height - 2

        return height
      }
    })
  }

  // Public methods
  return {
    init: function (id) {
      _element = KTUtil.getById(id)

      if (!_element) {
        return
      }

      // Initialize
      _init()
    }
  }
}())

export default KTLayoutQuickCartPanel
