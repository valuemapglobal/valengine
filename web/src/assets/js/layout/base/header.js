'use strict'

const KTLayoutHeader = (function () {
  // Private properties
  let _element
  let _elementForMobile
  let _object

  // Get height
  const _getHeight = function () {
    let height = 0

    if (_element) {
      height = KTUtil.actualHeight(_element) + 1
    }

    return height
  }

  // Get height
  const _getHeightForMobile = function () {
    let height

    height = KTUtil.actualHeight(_elementForMobile)

    return height
  }

  // Public methods
  return {
    init: function (id, idForMobile) {
      _element = KTUtil.getById(id)
      _elementForMobile = KTUtil.getById(idForMobile)

      if (!_element) {

      }
    },

    isFixed: function () {
      return KTUtil.hasClass(KTUtil.getBody(), 'header-fixed')
    },

    isFixedForMobile: function () {
      return KTUtil.hasClass(KTUtil.getBody(), 'header-mobile-fixed')
    },

    getElement: function () {
      return _element
    },

    getElementForMobile: function () {
      return _elementForMobile
    },

    getHeader: function () {
      return _object
    },

    getHeight: function () {
      return _getHeight()
    },

    getHeightForMobile: function () {
      return _getHeightForMobile()
    }
  }
}())

export default KTLayoutHeader
