'use strict'

const KTLayoutStickyCard = (function () {
  // Private properties
  let _element
  let _object

  // Private functions
  const _init = function () {
    let offset = 300

    if (typeof KTLayoutHeader !== 'undefined') {
      offset = KTLayoutHeader.getHeight()
    }

    _object = new KTCard(_element, {
      sticky: {
        offset: offset,
        zIndex: 90,
        position: {
          top: function () {
            let pos = 0
            const body = KTUtil.getBody()

            if (KTUtil.isBreakpointUp('lg')) {
              if (typeof KTLayoutHeader !== 'undefined' && KTLayoutHeader.isFixed()) {
                pos = pos + KTLayoutHeader.getHeight()
              }

              if (typeof KTLayoutSubheader !== 'undefined' && KTLayoutSubheader.isFixed()) {
                pos = pos + KTLayoutSubheader.getHeight()
              }
            } else {
              if (typeof KTLayoutHeader !== 'undefined' && KTLayoutHeader.isFixedForMobile()) {
                pos = pos + KTLayoutHeader.getHeightForMobile()
              }
            }

            pos = pos - 1 // remove header border width

            return pos
          },
          left: function (card) {
            return KTUtil.offset(_element).left
          },
          right: function (card) {
            const body = KTUtil.getBody()

            const cardWidth = parseInt(KTUtil.css(_element, 'width'))
            const bodyWidth = parseInt(KTUtil.css(body, 'width'))
            const cardOffsetLeft = KTUtil.offset(_element).left

            return bodyWidth - cardWidth - cardOffsetLeft
          }
        }
      }
    })

    _object.initSticky()

    KTUtil.addResizeHandler(function () {
      _object.updateSticky()
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
    },

    update: function () {
      if (_object) {
        _object.updateSticky()
      }
    }
  }
}())

export default KTLayoutStickyCard
