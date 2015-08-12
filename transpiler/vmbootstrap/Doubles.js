goog.module('vmbootstrap.DoublesModule');


var Class = goog.require('gen.java.lang.CoreModule').Class;
var Long = goog.require('nativebootstrap.LongUtilsModule').Long;
var $double = goog.require('vmbootstrap.PrimitivesModule').$double;
var Primitives = goog.require('vmbootstrap.PrimitivesModule').Primitives;

// TODO(rluble): Maybe this should be merged with $double
/**
 * Provides devirtualized method implementations for Doubles.
 */
class Doubles {
  /**
   * @param {*} obj
   * @param {*} other
   * @return {boolean}
   * @public
   */
  static m_equals__java_lang_Object__java_lang_Object(obj, other) {
    return obj === other;
  }

  /**
   * @param {*} obj
   * @return {number}
   * @public
   */
  static m_hashCode__java_lang_Object(obj) {
    return /** @type {number} */ (obj);
  }

  /**
   * @param {*} obj
   * @return {?string}
   * @public
   */
  static m_toString__java_lang_Object(obj) {
    return  /** @type {Object} */ (obj).toString();
  }

  /**
   * @param {*} obj
   * @return {Class}
   * @public
   */
  static m_getClass__java_lang_Object(obj) {
    return $double.$class;
  }

  /**
   * @param {number} obj
   * @return {number}
   * @public
   */
  static m_byteValue__java_lang_Double(obj) {
    return Primitives.$castDoubleToByte(obj);
  }

  /**
   * @param {number} obj
   * @return {number}
   * @public
   */
  static m_doubleValue__java_lang_Double(obj) {
    return obj;
  }

  /**
   * @param {number} obj
   * @return {number}
   * @public
   */
  static m_floatValue__java_lang_Double(obj) {
    return obj;
  }

  /**
   * @param {number} obj
   * @return {number}
   * @public
   */
  static m_intValue__java_lang_Double(obj) {
    return Primitives.$castDoubleToInt(obj);
  }

  /**
   * @param {number} obj
   * @return {!Long}
   * @public
   */
  static m_longValue__java_lang_Double(obj) {
    return Primitives.$castDoubleToLong(obj);
  }

  /**
   * @param {number} obj
   * @return {number}
   * @public
   */
  static m_shortValue__java_lang_Double(obj) {
    return Primitives.$castDoubleToShort(obj);
  }

  /**
   * @param {number} obj
   * @return {number}
   * @public
   */
  static $create__double(obj) {
    return obj;
  }
};


/**
 * Exported class.
 */
exports.Doubles = Doubles;
