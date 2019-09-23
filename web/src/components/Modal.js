import React from 'react';
import './Modal.css';

class Modal extends React.Component {
  handleChange({ target }) {
    this.setState({
      value: target.value,
    });
  }

  handleButton(event, operator) {
    this.props.processModal(this.state.value, operator);

    this.setState({
      value: null,
    });
  }

  close(event) {
    this.props.closeModal();
  }

  render() {
    return (
      <div className="Modal ModalOn">
        <div className="ModalContent">
          <span onClick={(event) => this.close(event)} className="Close">&times;</span>
          <div>
            <input autoFocus type="number" onChange={event => this.handleChange(event)}></input>
          </div>
          <div>
            <button className="ModalButton" onClick={(event) => this.handleButton(event, "+")}>+</button>
            <button className="ModalButton" onClick={(event) => this.handleButton(event, "-")}>-</button>
            <button className="ModalButton" onClick={(event) => this.handleButton(event, "x")}>x</button>
            <button className="ModalButton" onClick={(event) => this.handleButton(event, "/")}>/</button>
            <button className="ModalButton" onClick={(event) => this.handleButton(event, "")}>&nbsp;</button>
          </div>
        </div>
      </div>
    );
  }
}

export default Modal;
