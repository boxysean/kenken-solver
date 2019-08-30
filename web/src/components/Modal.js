import React from 'react';
import './Modal.css';

class Modal extends React.Component {
  handleChange({ target }) {
    console.log(target.value);
    this.setState({
      value: target.value,
    });
  }

  handleButton(event, operator) {
    console.log("HANDLE BUTTON!");

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
          <input autoFocus onChange={event => this.handleChange(event)}></input>
          <button onClick={(event) => this.handleButton(event, "+")}>+</button>
          <button onClick={(event) => this.handleButton(event, "-")}>-</button>
          <button onClick={(event) => this.handleButton(event, "*")}>*</button>
          <button onClick={(event) => this.handleButton(event, "/")}>/</button>
        </div>
      </div>
    );
  }
}

export default Modal;
