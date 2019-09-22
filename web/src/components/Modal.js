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
          <input autoFocus type="number" onChange={event => this.handleChange(event)}></input>
          <button onClick={(event) => this.handleButton(event, "+")}>+</button>
          <button onClick={(event) => this.handleButton(event, "-")}>-</button>
          <button onClick={(event) => this.handleButton(event, "x")}>x</button>
          <button onClick={(event) => this.handleButton(event, "/")}>/</button>
          <button onClick={(event) => this.handleButton(event, "")}>&nbsp;</button>
        </div>
      </div>
    );
  }
}

export default Modal;
